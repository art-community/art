package io.art.http.router;

import io.art.core.mime.*;
import io.art.core.property.*;
import io.art.http.configuration.*;
import io.art.http.state.*;
import io.art.meta.model.*;
import io.art.server.method.*;
import io.art.transport.payload.*;
import io.netty.buffer.*;
import lombok.*;
import org.reactivestreams.*;
import reactor.core.publisher.*;
import reactor.netty.http.websocket.*;
import static io.art.core.mime.MimeType.*;
import static io.art.core.property.LazyProperty.*;
import static io.art.http.module.HttpModule.*;
import static io.art.http.state.WsLocalState.*;
import static io.art.meta.model.TypedObject.*;
import static io.art.transport.constants.TransportModuleConstants.*;
import static io.art.transport.mime.MimeTypeDataFormatMapper.*;
import static io.art.transport.payload.TransportPayloadReader.*;
import static io.art.transport.payload.TransportPayloadWriter.*;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static java.util.Objects.*;
import static lombok.AccessLevel.*;
import java.util.function.*;

class WsRouting implements BiFunction<WebsocketInbound, WebsocketOutbound, Publisher<Void>> {
    private final ServiceMethod serviceMethod;
    private final MetaType<?> inputType;
    private final MetaType<?> outputType;
    private final HttpRouteConfiguration routeConfiguration;
    private final MimeType defaultMimeType;
    private final MetaType<?> inputMappingType;
    private final MetaType<?> outputMappingType;
    private final HttpModuleState state;
    private final MetaClass<?> owner;
    private final MetaMethod<?> delegate;

    @Builder(access = PACKAGE)
    private WsRouting(ServiceMethod serviceMethod,
                      HttpRouteConfiguration routeConfiguration,
                      MimeType defaultMimeType,
                      MetaType<?> inputMappingType,
                      MetaType<?> outputMappingType) {
        this.serviceMethod = serviceMethod;
        this.routeConfiguration = routeConfiguration;
        this.defaultMimeType = defaultMimeType;
        this.inputMappingType = inputMappingType;
        this.outputMappingType = outputMappingType;
        state = httpModule().state();
        owner = serviceMethod.getInvoker().getOwner();
        delegate = serviceMethod.getInvoker().getDelegate();
        inputType = serviceMethod.getInputType();
        outputType = serviceMethod.getOutputType();
    }

    @Override
    public Publisher<Void> apply(WebsocketInbound inbound, WebsocketOutbound outbound) {
        DataFormat inputDataFormat = fromMimeType(parseMimeType(inbound.headers().get(CONTENT_TYPE), defaultMimeType));
        DataFormat outputDataFormat = fromMimeType(parseMimeType(inbound.headers().get(ACCEPT), defaultMimeType));
        TransportPayloadReader reader = transportPayloadReader(inputDataFormat);
        TransportPayloadWriter writer = transportPayloadWriter(outputDataFormat);

        LazyProperty<WsLocalState> localState = lazy(() -> wsLocalState(inbound, outbound, routeConfiguration));
        state.wsState(owner, delegate, localState);

        if (isNull(inputType)) {
            Flux<ByteBuf> output = serviceMethod.serve(Flux.empty()).map(value -> writer.write(typed(outputMappingType, value)));
            return localState.initialized() ? localState.get().outbound().send(output).then() : outbound.send(output).then();
        }

        Flux<Object> input = (localState.initialized() ? localState.get().inbound() : inbound)
                .aggregateFrames()
                .receive()
                .map(data -> reader.read(data, inputMappingType))
                .filter(data -> !data.isEmpty())
                .map(TransportPayload::getValue);

        Flux<ByteBuf> output = serviceMethod
                .serve(input)
                .map(value -> writer.write(typed(outputMappingType, value)));

        return localState.initialized() ? localState.get().outbound().send(output).then() : outbound.send(output).then();
    }
}
