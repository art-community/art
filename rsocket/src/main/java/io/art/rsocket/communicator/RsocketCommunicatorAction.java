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

package io.art.rsocket.communicator;

import io.art.communicator.*;
import io.art.core.model.*;
import io.art.logging.logger.*;
import lombok.*;
import reactor.core.publisher.*;
import static io.art.logging.module.LoggingModule.*;
import static io.art.transport.mime.MimeTypeDataFormatMapper.toMimeType;
import static lombok.AccessLevel.*;

@Builder(toBuilder = true)
public class RsocketCommunicatorAction implements Communication {
    private final CommunicatorActionIdentifier communicatorActionId;

    @Getter(lazy = true, value = PRIVATE)
    private final static Logger logger = logger(RsocketCommunicatorAction.class);
/*

    @Getter(lazy = true, value = PRIVATE)
    private final CommunicatorAction communicatorAction = communicatorAction();

    private final Property<RSocketClient> client = property(this::createClient, this::disposeClient).listenConsumer(() -> consumer()
            .connectorConsumers()
            .consumerFor(connectorConfiguration().getConnectorId()));

    private final Property<Function<Flux<Value>, Flux<Value>>> communication = property(this::communication)
            .listenProperties(client);
*/

    @Override
    public void dispose() {
        //client.dispose();
    }

    @Override
    public Flux<Object> communicate(Flux<Object> input) {
        return null;
    }

/*    private RSocketClient createClient() {
        RsocketConnectorConfiguration connectorConfiguration = connectorConfiguration();
        RSocketConnector connector = RSocketConnector.create()
                .payloadDecoder(connectorConfiguration.getPayloadDecoderMode() == PayloadDecoderMode.ZERO_COPY ? PayloadDecoder.ZERO_COPY : PayloadDecoder.DEFAULT)
                .dataMimeType(toMimeType(connectorConfiguration.getDataFormat()).toString())
                .metadataMimeType(toMimeType(connectorConfiguration.getMetaDataFormat()).toString())
                .fragment(connectorConfiguration.getFragment())
                .interceptors(registry -> configureInterceptors(connectorConfiguration, registry));
        apply(connectorConfiguration.getKeepAlive(), keepAlive -> connector.keepAlive(keepAlive.getInterval(), keepAlive.getMaxLifeTime()));
        apply(connectorConfiguration.getResume(), resume -> connector.resume(resume.toResume()));
        apply(connectorConfiguration.getRetry(), retry -> connector.reconnect(retry.toRetry()));
        TransportPayloadWriter setupPayloadWriter = communicatorModule().configuration().getWriter(communicatorActionId, connectorConfiguration.getDataFormat());
        RsocketSetupPayload setupPayload = RsocketSetupPayload.builder()
                .dataFormat(connectorConfiguration.getDataFormat())
                .metadataFormat(connectorConfiguration.getMetaDataFormat())
                .id(getCommunicatorAction().getTargetServiceMethod())
                .build();
        Payload payload = DefaultPayload.create(setupPayloadWriter.write(setupPayload.toEntity()).nioBuffer());
        connector.setupPayload(payload);
        switch (connectorConfiguration.getTransport()) {
            case TCP:
                TcpClient tcpClient = connectorConfiguration.getTcpClient();
                int tcpMaxFrameLength = connectorConfiguration.getTcpMaxFrameLength();
                Mono<RSocket> socket = connector
                        .connect(TcpClientTransport.create(tcpClient, tcpMaxFrameLength))
                        .doOnTerminate(payload::release)
                        .doOnError(throwable -> getLogger().error(throwable.getMessage(), throwable));
                if (connectorConfiguration.isLogging()) {
                    socket = socket.doOnSubscribe(subscription -> getLogger().info(format(COMMUNICATOR_STARTED, connectorConfiguration.getConnectorId(), setupPayload)));
                }
                return from(socket.blockOptional().orElseThrow(ImpossibleSituationException::new));
            case WS:
                HttpClient httpWebSocketClient = connectorConfiguration.getHttpWebSocketClient();
                String httpWebSocketPath = connectorConfiguration.getHttpWebSocketPath();
                socket = connector
                        .connect(WebsocketClientTransport.create(httpWebSocketClient, httpWebSocketPath))
                        .doOnTerminate(payload::release)
                        .doOnError(throwable -> getLogger().error(throwable.getMessage(), throwable));
                if (connectorConfiguration.isLogging()) {
                    socket = socket.doOnSubscribe(subscription -> getLogger().info(format(COMMUNICATOR_STARTED, connectorConfiguration.getConnectorId(), setupPayload)));
                }
                return from(socket.blockOptional().orElseThrow(ImpossibleSituationException::new));
        }
        throw new ImpossibleSituationException();
    }

    private void configureInterceptors(RsocketConnectorConfiguration connectorConfiguration, InterceptorRegistry registry) {
        String connectorId = connectorConfiguration.getConnectorId();
        registry.forResponder(new RsocketConnectorLoggingInterceptor(connectorId))
                .forRequester(new RsocketConnectorLoggingInterceptor(connectorId));
    }

    private void disposeClient(RSocketClient rsocket) {
        RsocketConnectorConfiguration connectorConfiguration = connectorConfiguration();
        disposeRsocket(rsocket);
        if (connectorConfiguration.isLogging()) {
            getLogger().info(format(COMMUNICATOR_STOPPED, connectorConfiguration.getConnectorId()));
        }
    }

    @SuppressWarnings(CONSTANT_CONDITIONS)
    private CommunicatorAction communicatorAction() {
        return communicatorModule()
                .configuration()
                .getRegistry()
                .findActionById(communicatorActionId)
                .orElseThrow(ImpossibleSituationException::new);
    }

    private RsocketConnectorConfiguration connectorConfiguration() {
        RsocketCommunicatorConfiguration communicatorConfiguration = rsocketModule().configuration().getCommunicatorConfiguration();
        return communicatorModule()
                .configuration()
                .findConnectorId(RSOCKET.getProtocol(), communicatorActionId)
                .map(communicatorConfiguration.getConnectorConfigurations()::get)
                .orElseGet(() -> communicatorConfiguration.getConnectorConfigurations().get(communicatorActionId.getCommunicatorId()));
    }

    private Function<Flux<Value>, Flux<Value>> communication() {
        CommunicatorConfiguration configuration = communicatorModule().configuration();
        TransportPayloadReader reader = configuration.getReader(communicatorActionId, connectorConfiguration().getDataFormat());
        TransportPayloadWriter writer = configuration.getWriter(communicatorActionId, connectorConfiguration().getDataFormat());
        RSocketClient client = this.client.get();
        switch (communicationMode()) {
            case FIRE_AND_FORGET:
                return input -> cast(client.fireAndForget(input.map(value -> create(writer.write(value))).last(EMPTY_PAYLOAD)).flux());
            case REQUEST_RESPONSE:
                return input -> client
                        .requestResponse(input.map(value -> create(writer.write(value))).last(EMPTY_PAYLOAD))
                        .flux()
                        .map(payload -> readRsocketPayload(reader, payload))
                        .filter(data -> !data.isEmpty())
                        .map(TransportPayload::getValue);
            case REQUEST_STREAM:
                return input -> client
                        .requestStream(input.map(value -> create(writer.write(value))).last(EMPTY_PAYLOAD))
                        .map(payload -> readRsocketPayload(reader, payload))
                        .filter(data -> !data.isEmpty())
                        .map(TransportPayload::getValue);
            case REQUEST_CHANNEL:
                return input -> client
                        .requestChannel(input.map(value -> create(writer.write(value))).switchIfEmpty(EMPTY_PAYLOAD_MONO.get()))
                        .map(payload -> readRsocketPayload(reader, payload))
                        .filter(data -> !data.isEmpty())
                        .map(TransportPayload::getValue);
            case METADATA_PUSH:
                return input -> cast(client.metadataPush(input.map(value -> create(writer.write(value))).last(EMPTY_PAYLOAD)).flux());
        }
        throw new ImpossibleSituationException();
    }

    private CommunicationMode communicationMode() {
        CommunicatorAction communicatorAction = getCommunicatorAction();
        if (isNull(communicatorAction.getOutputMapper())) {
            return FIRE_AND_FORGET;
        }
        if (communicatorAction.getInputMode() == FLUX) {
            return REQUEST_CHANNEL;
        }
        if (communicatorAction.getOutputMode() == FLUX) {
            return REQUEST_STREAM;
        }
        return REQUEST_RESPONSE;
    }

    private RsocketModuleRefresher.Consumer consumer() {
        return rsocketModule().configuration().getConsumer();
    }*/
}
