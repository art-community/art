/*
 * ART Java
 *
 * Copyright 2019 ART
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.art.http.client.communicator;

import lombok.*;
import org.apache.http.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.nio.client.*;
import org.apache.http.nio.client.methods.*;
import org.zalando.logbook.httpclient.*;
import ru.art.core.constants.*;
import ru.art.core.mime.*;
import ru.art.entity.Value;
import ru.art.entity.interceptor.*;
import ru.art.entity.mapper.*;
import ru.art.http.client.exception.*;
import ru.art.http.client.handler.*;
import ru.art.http.client.interceptor.*;
import ru.art.http.constants.*;
import ru.art.http.mapper.*;
import static java.text.MessageFormat.*;
import static java.util.Objects.*;
import static java.util.Optional.*;
import static java.util.concurrent.CompletableFuture.*;
import static lombok.AccessLevel.*;
import static org.apache.http.client.methods.RequestBuilder.create;
import static org.apache.http.nio.client.methods.HttpAsyncMethods.*;
import static ru.art.core.caster.Caster.*;
import static ru.art.core.checker.CheckerForEmptiness.*;
import static ru.art.core.constants.InterceptionStrategy.*;
import static ru.art.core.extension.NullCheckingExtensions.*;
import static ru.art.http.client.body.descriptor.HttpBodyDescriptor.*;
import static ru.art.http.client.builder.HttpUriBuilder.*;
import static ru.art.http.client.constants.HttpClientExceptionMessages.*;
import static ru.art.http.client.module.HttpClientModule.*;
import static ru.art.logging.LoggingModule.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

@NoArgsConstructor(access = PACKAGE)
class HttpCommunicationExecutor {
    static <ResponseType> ResponseType executeHttpRequest(HttpCommunicationConfiguration configuration) {
        HttpUriRequest request = buildRequest(configuration);
        if (isNull(request)) {
            return null;
        }
        List<HttpClientInterceptor> requestInterceptors = configuration.getRequestInterceptors();
        for (HttpClientInterceptor requestInterceptor : requestInterceptors) {
            InterceptionStrategy strategy = requestInterceptor.interceptRequest(request);
            if (strategy == PROCESS_HANDLING) break;
            if (strategy == STOP_HANDLING) return null;
        }
        CloseableHttpClient client = getOrElse(configuration.getSynchronousClient(), httpClientModule().getClient());
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = client.execute(request);
            List<HttpClientInterceptor> responseInterceptors = configuration.getResponseInterceptors();
            for (HttpClientInterceptor responseInterceptor : responseInterceptors) {
                InterceptionStrategy strategy = responseInterceptor.interceptResponse(request, httpResponse);
                if (strategy == PROCESS_HANDLING) break;
                if (strategy == STOP_HANDLING) return null;
            }
            return parseResponse(configuration, httpResponse);
        } catch (Throwable throwable) {
            throw new HttpClientException(throwable);
        } finally {
            if (nonNull(httpResponse)) {
                try {
                    httpResponse.close();
                } catch (Throwable closableThrowable) {
                    loggingModule()
                            .getLogger(HttpCommunicationExecutor.class)
                            .error(closableThrowable.getMessage(), closableThrowable);
                }
            }
        }
    }

    static <ResponseType> CompletableFuture<Optional<ResponseType>> executeAsynchronousHttpRequest(HttpCommunicationConfiguration configuration) {
        HttpUriRequest httpUriRequest = buildRequest(configuration);
        if (isNull(httpUriRequest)) {
            return completedFuture(empty());
        }
        List<HttpClientInterceptor> requestInterceptors = configuration.getRequestInterceptors();
        for (HttpClientInterceptor requestInterceptor : requestInterceptors) {
            InterceptionStrategy strategy = requestInterceptor.interceptRequest(httpUriRequest);
            if (strategy == PROCESS_HANDLING) break;
            if (strategy == STOP_HANDLING) return completedFuture(empty());
        }
        CloseableHttpAsyncClient client = getOrElse(configuration.getAsynchronousClient(), httpClientModule().getAsynchronousClient());
        HttpAsynchronousClientCallback callback = new HttpAsynchronousClientCallback(configuration.getRequest(), httpUriRequest, configuration);

        return supplyAsync(() -> executeHttpUriRequest(httpUriRequest, client, callback), configuration.getAsynchronousFuturesExecutor())
                .thenApply(response -> ofNullable(parseResponse(configuration, response)));
    }

    private static HttpResponse executeHttpUriRequest(HttpUriRequest httpUriRequest, CloseableHttpAsyncClient client, HttpAsynchronousClientCallback callback) {
        try {
            if (httpClientModule().isEnableRawDataTracing()) {
                LogbookHttpAsyncResponseConsumer<HttpResponse> logbookConsumer = new LogbookHttpAsyncResponseConsumer<>(createConsumer());
                return client.execute(HttpAsyncMethods.create(httpUriRequest), logbookConsumer, callback).get();
            }
            return client.execute(httpUriRequest, callback).get();
        } catch (Exception throwable) {
            throw new HttpClientException(throwable);
        }
    }

    private static HttpUriRequest buildRequest(HttpCommunicationConfiguration configuration) {
        RequestBuilder requestBuilder = create(configuration.getMethodType().name())
                .setUri(buildUri(configuration.getUrl(), configuration.getPathParameters(), configuration.getQueryParameters()))
                .setConfig(configuration.getRequestConfig())
                .setCharset(configuration.getRequestContentCharset())
                .setVersion(configuration.getHttpProtocolVersion());
        configuration.getHeaders().forEach(requestBuilder::addHeader);
        if (isNull(configuration.getRequest())) {
            return requestBuilder.build();
        }
        ValueFromModelMapper<Object, ? extends Value> requestMapper = cast(configuration.getRequestMapper());
        MimeToContentTypeMapper consumesMimeTypeMapper;
        MimeType producesMimeType;
        if (isNull(requestMapper)
                || isNull(consumesMimeTypeMapper = configuration.getProducesMimeType())
                || isNull(producesMimeType = consumesMimeTypeMapper.getMimeType())) {
            return requestBuilder.build();
        }
        Value requestValue = requestMapper.map(configuration.getRequest());
        List<ValueInterceptor<Value, Value>> requestValueInterceptors = configuration.getRequestValueInterceptors();
        for (ValueInterceptor<Value, Value> requestValueInterceptor : requestValueInterceptors) {
            ValueInterceptionResult<Value, Value> result = requestValueInterceptor.intercept(requestValue);
            if (isNull(result)) {
                break;
            }
            requestValue = result.getOutValue();
            if (result.getNextInterceptionStrategy() == PROCESS_HANDLING) {
                break;
            }
            if (result.getNextInterceptionStrategy() == STOP_HANDLING) {
                return null;
            }
        }
        HttpContentMapper contentMapper = httpClientModule()
                .getContentMappers()
                .get(producesMimeType);
        if (isNull(contentMapper)) {
            throw new HttpClientException(format(REQUEST_CONTENT_TYPE_NOT_SUPPORTED, producesMimeType.toString()));
        }
        byte[] payload = contentMapper.getToContent().mapToBytes(requestValue, producesMimeType, configuration.getRequestContentCharset());
        if (isEmpty(payload)) {
            return requestBuilder.build();
        }
        EntityBuilder entityBuilder = EntityBuilder.create().setBinary(payload)
                .setContentType(consumesMimeTypeMapper.getContentType())
                .setContentEncoding(configuration.getRequestContentEncoding());
        if (configuration.isGzipCompressedBody()) entityBuilder.gzipCompress();
        if (configuration.isChunkedBody()) entityBuilder.chunked();
        return requestBuilder.setEntity(entityBuilder.build()).build();
    }

    private static <ResponseType> ResponseType parseResponse(HttpCommunicationConfiguration configuration, HttpResponse httpResponse) {
        byte[] bytes = readResponseBody(httpResponse.getEntity());
        if (isEmpty(bytes)) return null;
        MimeToContentTypeMapper consumesMimeTypeMapper = configuration.getConsumesMimeType();
        MimeType consumesMimeType;
        if (isNull(consumesMimeTypeMapper) || isNull(consumesMimeType = consumesMimeTypeMapper.getMimeType()))
            return null;
        Header contentType = httpResponse.getEntity().getContentType();
        MimeType responseContentType = isNull(contentType) || configuration.isIgnoreResponseContentType()
                ? consumesMimeType
                : MimeType.valueOf(contentType.getValue());
        HttpContentMapper contentMapper = httpClientModule()
                .getContentMappers()
                .get(responseContentType);
        if (isNull(contentMapper)) {
            throw new HttpClientException(format(RESPONSE_CONTENT_TYPE_NOT_SUPPORTED, responseContentType.toString()));
        }
        Value responseValue = contentMapper.getFromContent().mapFromBytes(bytes, responseContentType, configuration.getRequestContentCharset());
        List<ValueInterceptor<Value, Value>> responseValueInterceptors = configuration.getResponseValueInterceptors();
        for (ValueInterceptor<Value, Value> responseValueInterceptor : responseValueInterceptors) {
            ValueInterceptionResult<Value, Value> result = responseValueInterceptor.intercept(responseValue);
            if (isNull(result)) {
                break;
            }
            responseValue = result.getOutValue();
            if (result.getNextInterceptionStrategy() == PROCESS_HANDLING) {
                break;
            }
            if (result.getNextInterceptionStrategy() == STOP_HANDLING) {
                return null;
            }
        }
        ValueToModelMapper<Object, ? extends Value> responseMapper = cast(configuration.getResponseMapper());
        if (isNull(responseValue) || isNull(responseMapper)) return null;
        return cast(responseMapper.map(cast(responseValue)));
    }

    @AllArgsConstructor(access = PACKAGE)
    static class HttpAsynchronousClientCallback implements FutureCallback<HttpResponse> {
        private final Object request;
        private final HttpUriRequest httpUriRequest;
        private final HttpCommunicationConfiguration configuration;

        @Override
        public void completed(HttpResponse result) {
            List<HttpClientInterceptor> responseInterceptors = configuration.getResponseInterceptors();
            for (HttpClientInterceptor responseInterceptor : responseInterceptors) {
                InterceptionStrategy strategy = responseInterceptor.interceptResponse(httpUriRequest, result);
                if (strategy == PROCESS_HANDLING) break;
                if (strategy == STOP_HANDLING) return;
            }
            try {
                HttpCommunicationResponseHandler<?, ?> completionHandler = configuration.getCompletionHandler();
                if (nonNull(completionHandler)) {
                    completionHandler.completed(ofNullable(cast(request)), ofNullable(cast(parseResponse(configuration, result))));
                }
            } catch (Throwable throwable) {
                HttpCommunicationExceptionHandler<?> exceptionHandler = configuration.getExceptionHandler();
                if (nonNull(exceptionHandler)) {
                    exceptionHandler.failed(ofNullable(cast(request)), throwable);
                }
            }
        }

        @Override
        public void failed(Exception exception) {
            HttpCommunicationExceptionHandler<?> exceptionHandler = configuration.getExceptionHandler();
            if (nonNull(exceptionHandler)) {
                exceptionHandler.failed(ofNullable(cast(request)), exception);
            }
        }

        @Override
        public void cancelled() {
            HttpCommunicationCancellationHandler<?> cancellationHandler = configuration.getCancellationHandler();
            if (nonNull(cancellationHandler)) {
                cancellationHandler.cancelled(ofNullable(cast(request)));
            }
        }
    }
}
