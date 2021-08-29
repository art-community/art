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
import io.art.http.state.*;
import io.art.logging.*;
import io.art.meta.invoker.*;
import io.art.meta.model.*;
import io.art.server.configuration.*;
import io.art.server.method.*;
import io.art.transport.constants.TransportModuleConstants.*;
import io.art.transport.payload.*;
import io.netty.handler.codec.http.*;
import lombok.*;
import org.reactivestreams.*;
import reactor.core.publisher.*;
import reactor.netty.http.server.*;
import reactor.netty.http.websocket.*;
import static io.art.core.checker.ModuleChecker.*;
import static io.art.core.checker.NullityChecker.*;
import static io.art.core.constants.BufferConstants.*;
import static io.art.core.mime.MimeType.*;
import static io.art.http.constants.HttpModuleConstants.*;
import static io.art.http.constants.HttpModuleConstants.Messages.*;
import static io.art.http.constants.HttpModuleConstants.Warnings.*;
import static io.art.http.module.HttpModule.*;
import static io.art.http.state.HttpLocalState.*;
import static io.art.meta.constants.MetaConstants.MetaTypeInternalKind.*;
import static io.art.meta.model.TypedObject.*;
import static io.art.transport.mime.MimeTypeDataFormatMapper.*;
import static io.art.transport.payload.TransportPayloadReader.*;
import static io.art.transport.payload.TransportPayloadWriter.*;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static java.text.MessageFormat.*;
import static java.util.Objects.*;
import java.nio.file.*;
import java.util.function.*;

public class HttpRouter {
    private final HttpModuleState state;
    private HttpServerConfiguration httpConfiguration;

    public HttpRouter(HttpServerRoutes routes, HttpServerConfiguration httpConfiguration, ServerConfiguration serverConfiguration) {
        this.httpConfiguration = httpConfiguration;
        state = httpModule().state();
        setupRoutes(routes, serverConfiguration);
    }

    private void setupRoutes(HttpServerRoutes routes, ServerConfiguration serverConfiguration) {
        for (HttpRouteConfiguration route : httpConfiguration.getRoutes().get()) {
            HttpRouteType httpMethodType = route.getType();
            ServiceMethodIdentifier serviceMethodId = route.getServiceMethodId();
            String path = route.getPath().route(serviceMethodId);
            ServiceMethod serviceMethod = serverConfiguration.getMethods().get().get(serviceMethodId);
            switch (httpMethodType) {
                case GET:
                    routes.get(path, handleHttp(serviceMethod, route));
                    break;
                case POST:
                    routes.post(path, handleHttp(serviceMethod, route));
                    break;
                case PUT:
                    routes.put(path, handleHttp(serviceMethod, route));
                    break;
                case DELETE:
                    routes.delete(path, handleHttp(serviceMethod, route));
                    break;
                case OPTIONS:
                    routes.options(path, handleHttp(serviceMethod, route));
                    break;
                case HEAD:
                    routes.head(path, handleHttp(serviceMethod, route));
                    break;
                case WS:
                    routes.ws(path, handleWebsocket(serviceMethod, route));
                    break;
                case PATH:
                    Path routePath = route.getPathConfiguration().getPath();
                    if (withLogging() && !routePath.toFile().exists()) {
                        Logging.logger(HTTP_SERVER_LOGGER).warn(format(ROUTE_PATH_NOT_EXISTS, routePath.toAbsolutePath()));
                        break;
                    }
                    if (routePath.toFile().isDirectory()) {
                        routes.directory(path, routePath);
                        break;
                    }
                    routes.file(path, routePath);
                    break;
            }
        }
    }

    private WsRouting handleWebsocket(ServiceMethod serviceMethod, HttpRouteConfiguration routeConfiguration) {
        DataFormat defaultDataFormat = orElse(routeConfiguration.getDefaultDataFormat(), httpConfiguration.getDefaultDataFormat());
        MimeType defaultMimeType = toMimeType(defaultDataFormat);
        MetaType<?> inputMappingType = serviceMethod.getInputType();
        if (nonNull(inputMappingType) && (inputMappingType.internalKind() == MONO || inputMappingType.internalKind() == FLUX)) {
            inputMappingType = inputMappingType.parameters().get(0);
        }
        MetaType<?> outputMappingType = serviceMethod.getOutputType();
        if (nonNull(outputMappingType) && (outputMappingType.internalKind() == MONO || outputMappingType.internalKind() == FLUX)) {
            outputMappingType = outputMappingType.parameters().get(0);
        }
        return new WsRouting(
                serviceMethod,
                routeConfiguration,
                defaultMimeType,
                inputMappingType,
                outputMappingType
        );
    }

    private HttpRouting handleHttp(ServiceMethod serviceMethod, HttpRouteConfiguration routeConfiguration) {
        DataFormat defaultDataFormat = orElse(routeConfiguration.getDefaultDataFormat(), httpConfiguration.getDefaultDataFormat());
        MimeType defaultMimeType = toMimeType(defaultDataFormat);
        MetaType<?> inputMappingType = serviceMethod.getInputType();
        if (nonNull(inputMappingType) && (inputMappingType.internalKind() == MONO || inputMappingType.internalKind() == FLUX)) {
            inputMappingType = inputMappingType.parameters().get(0);
        }
        MetaType<?> outputMappingType = serviceMethod.getOutputType();
        if (nonNull(outputMappingType) && (outputMappingType.internalKind() == MONO || outputMappingType.internalKind() == FLUX)) {
            outputMappingType = outputMappingType.parameters().get(0);
        }
        return new HttpRouting(
                serviceMethod,
                routeConfiguration,
                defaultMimeType,
                inputMappingType,
                outputMappingType
        );
    }

    @RequiredArgsConstructor
    private static class WsRouting implements BiFunction<WebsocketInbound, WebsocketOutbound, Publisher<Void>> {
        final ServiceMethod serviceMethod;
        final HttpRouteConfiguration routeConfiguration;
        final MimeType defaultMimeType;
        final MetaType<?> inputMappingType;
        final MetaType<?> outputMappingType;

        @Override
        public Publisher<Void> apply(WebsocketInbound inbound, WebsocketOutbound outbound) {
            DataFormat inputDataFormat = fromMimeType(parseMimeType(inbound.headers().get(CONTENT_TYPE), defaultMimeType));
            DataFormat outputDataFormat = fromMimeType(parseMimeType(inbound.headers().get(ACCEPT), defaultMimeType));
            TransportPayloadReader reader = transportPayloadReader(inputDataFormat);
            TransportPayloadWriter writer = transportPayloadWriter(outputDataFormat);

            Flux<Object> input = inbound.receive()
                    .map(data -> reader.read(data, inputMappingType))
                    .filter(data -> !data.isEmpty())
                    .map(TransportPayload::getValue);

            if (isNull(outputMappingType) || outputMappingType.internalKind() == VOID) {
                return outbound
                        .send(serviceMethod.serve(input).map(ignore -> EMPTY_NETTY_BUFFER))
                        .then();
            }

            return outbound
                    .send(serviceMethod
                            .serve(input)
                            .map(value -> writer.write(typed(outputMappingType, value))))
                    .then();
        }
    }

    @RequiredArgsConstructor
    private class HttpRouting implements BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> {
        final ServiceMethod serviceMethod;
        final HttpRouteConfiguration routeConfiguration;
        final MimeType defaultMimeType;
        final MetaType<?> inputMappingType;
        final MetaType<?> outputMappingType;

        @Override
        public Publisher<Void> apply(HttpServerRequest request, HttpServerResponse response) {
            HttpHeaders headers = request.requestHeaders();
            DataFormat inputDataFormat = fromMimeType(parseMimeType(headers.get(CONTENT_TYPE), defaultMimeType));
            DataFormat outputDataFormat = fromMimeType(parseMimeType(headers.get(ACCEPT), defaultMimeType));
            TransportPayloadReader reader = transportPayloadReader(inputDataFormat);
            TransportPayloadWriter writer = transportPayloadWriter(outputDataFormat);

            MetaMethodInvoker invoker = serviceMethod.getInvoker();
            state.httpState(invoker.getOwner(), invoker.getDelegate(), httpLocalState(request, response));

            Flux<Object> input = request.receive()
                    .map(data -> reader.read(data, inputMappingType))
                    .filter(data -> !data.isEmpty())
                    .map(TransportPayload::getValue);

            if (isNull(outputMappingType) || outputMappingType.internalKind() == VOID) {
                return response
                        .send(serviceMethod.serve(input).map(ignore -> EMPTY_NETTY_BUFFER))
                        .then();
            }

            return response
                    .send(serviceMethod
                            .serve(input)
                            .map(value -> writer.write(typed(outputMappingType, value))))
                    .then();
        }
    }
}
