/*
 * ART
 *
 * Copyright 2019-2021 ART
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

package io.art.http.router;

import io.art.core.mime.*;
import io.art.core.model.*;
import io.art.http.configuration.*;
import io.art.http.exception.*;
import io.art.http.state.*;
import io.art.server.method.*;
import io.art.transport.payload.*;
import io.art.value.constants.ValueModuleConstants.*;
import io.art.value.immutable.*;
import io.netty.buffer.*;
import org.reactivestreams.*;
import reactor.core.publisher.*;
import reactor.netty.http.server.*;
import reactor.netty.http.websocket.*;
import reactor.util.context.*;
import static io.art.core.caster.Caster.*;
import static io.art.core.model.ServiceMethodIdentifier.*;
import static io.art.core.wrapper.ExceptionWrapper.*;
import static io.art.http.constants.HttpModuleConstants.Errors.*;
import static io.art.http.constants.HttpModuleConstants.*;
import static io.art.http.module.HttpModule.*;
import static io.art.server.constants.ServerConstants.StateKeys.*;
import static io.art.server.module.ServerModule.*;
import static io.art.server.state.ServerModuleState.ServerThreadLocalState.*;
import static io.art.value.mime.MimeTypeDataFormatMapper.*;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static java.text.MessageFormat.*;
import static java.util.Objects.*;
import java.nio.file.*;
import java.util.*;

public class HttpRouter {
    public HttpRouter(HttpServerRoutes routes, HttpServerConfiguration configuration) {
        for (Map.Entry<String, HttpServiceConfiguration> service : configuration.getServices().entrySet()) {
            for (Map.Entry<String, HttpMethodConfiguration> method : service.getValue().getMethods().entrySet()) {
                HttpMethodConfiguration methodValue = method.getValue();
                HttpMethodType httpMethodType = methodValue.getMethod();
                switch (httpMethodType) {
                    case GET:
                        routes.get(methodValue.getPath(), (request, response) -> handleHttp(findSpecification(serviceMethodId(service.getKey(), method.getKey())), request, response));
                        break;
                    case POST:
                        routes.post(methodValue.getPath(), (request, response) -> handleHttp(findSpecification(serviceMethodId(service.getKey(), method.getKey())), request, response));
                        break;
                    case PUT:
                        routes.put(methodValue.getPath(), (request, response) -> handleHttp(findSpecification(serviceMethodId(service.getKey(), method.getKey())), request, response));
                        break;
                    case DELETE:
                        routes.delete(methodValue.getPath(), (request, response) -> handleHttp(findSpecification(serviceMethodId(service.getKey(), method.getKey())), request, response));
                        break;
                    case OPTIONS:
                        routes.options(methodValue.getPath(), (request, response) -> handleHttp(findSpecification(serviceMethodId(service.getKey(), method.getKey())), request, response));
                        break;
                    case HEAD:
                        routes.head(methodValue.getPath(), (request, response) -> handleHttp(findSpecification(serviceMethodId(service.getKey(), method.getKey())), request, response));
                        break;
                    case WEBSOCKET:
                        routes.ws(methodValue.getPath(), (inbound, outbound) -> handleWebsocket(findSpecification(serviceMethodId(service.getKey(), method.getKey())), inbound, outbound));
                        break;
                    case FILE:
                        routes.file(methodValue.getPath(), methodValue.getFilePath());
                        break;
                    case DIRECTORY:
                        routes.directory(methodValue.getPath(), Paths.get(methodValue.getFilePath()));
                        break;
                }
            }
        }
    }

    private Publisher<Void> handleWebsocket(ServiceMethod specification, WebsocketInbound inbound, WebsocketOutbound outbound) {
        DataFormat defaultDataFormat = findMethodConfiguration(specification).getDefaultDataFormat();
        DataFormat inputDataFormat = ignoreException(
                () -> fromMimeType(MimeType.parseMimeType(inbound.headers().get(CONTENT_TYPE))),
                ignored -> defaultDataFormat);
        DataFormat outputDataFormat = ignoreException(
                () -> fromMimeType(MimeType.parseMimeType(inbound.headers().get(ACCEPT))),
                ignored -> defaultDataFormat);
        TransportPayloadReader reader = specification
                .getConfiguration()
                .getReader(serviceMethodId(specification.getServiceId(), specification.getMethodId()), inputDataFormat);
        TransportPayloadWriter writer = specification
                .getConfiguration()
                .getWriter(serviceMethodId(specification.getServiceId(), specification.getMethodId()), outputDataFormat);
        return inbound.receive()
                .map(reader::read)
                .map(TransportPayload::getValue)
                .transform(specification::serve)
                .map(writer::write)
                .transform(outbound::send)
                .then();

    }

    private Publisher<Void> handleHttp(ServiceMethod specification, HttpServerRequest request, HttpServerResponse response) {
        DataFormat defaultDataFormat = findMethodConfiguration(specification).getDefaultDataFormat();
        DataFormat inputDataFormat = ignoreException(
                () -> fromMimeType(MimeType.parseMimeType(request.requestHeaders().get(CONTENT_TYPE))),
                ignored -> defaultDataFormat);
        DataFormat outputDataFormat = ignoreException(
                () -> fromMimeType(MimeType.parseMimeType(request.requestHeaders().get(ACCEPT))),
                ignored -> defaultDataFormat);

        Sinks.Many<ByteBuf> unicast = Sinks.many().unicast().onBackpressureBuffer();
        TransportPayloadReader reader = specification
                .getConfiguration()
                .getReader(serviceMethodId(specification.getServiceId(), specification.getMethodId()), inputDataFormat);
        TransportPayloadWriter writer = specification
                .getConfiguration()
                .getWriter(serviceMethodId(specification.getServiceId(), specification.getMethodId()), outputDataFormat);

        return request.receive()
                .map(reader::read)
                .map(TransportPayload::getValue)
                .transform(specification::serve)
                .onErrorResume(throwable -> mapException(specification, throwable))
                .map(writer::write)
                .contextWrite(ctx -> setContext(ctx
                        .put(HttpContext.class, HttpContext.from(request, response))
                        .put(SERVICE_METHOD_ID, specification))
                )
                .doOnNext(unicast::tryEmitNext)
                .doOnComplete(unicast::tryEmitComplete)
                .doOnError(unicast::tryEmitError)
                .thenMany(response.send(unicast.asFlux()));
    }

    private Flux<Value> mapException(ServiceMethod specification, Throwable exception) {
        Object result = httpModule().configuration().getServerConfiguration()
                .getExceptionMapper()
                .apply(cast(exception));
        return isNull(result) ?
                Flux.empty() :
                Flux.just(specification.getOutputType().map(cast(result)));
    }

    private Context setContext(Context context) {
        serverModule().state().localState(fromContext(context));
        return context;
    }

    private ServiceMethod findSpecification(ServiceMethodIdentifier serviceMethodId) {
        return specifications()
                .findMethodById(serviceMethodId)
                .orElseThrow(() -> new HttpException(format(SPECIFICATION_NOT_FOUND, serviceMethodId)));
    }

    private HttpMethodConfiguration findMethodConfiguration(ServiceMethod specification) {
        return httpModule().configuration().getServerConfiguration()
                .getServices().get(specification.getServiceId())
                .getMethods().get(specification.getMethodId());
    }
}
