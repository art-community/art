package io.art.rsocket.communicator;

import io.art.core.model.*;
import io.art.logging.*;
import io.art.logging.logger.*;
import io.art.rsocket.configuration.*;
import io.art.rsocket.configuration.common.*;
import io.art.rsocket.configuration.communicator.common.*;
import io.art.rsocket.configuration.communicator.tcp.*;
import io.art.rsocket.configuration.communicator.ws.*;
import io.art.rsocket.exception.*;
import io.art.rsocket.interceptor.*;
import io.art.rsocket.model.*;
import io.art.rsocket.model.RsocketSetupPayload.*;
import io.netty.handler.ssl.*;
import io.rsocket.*;
import io.rsocket.core.*;
import io.rsocket.frame.decoder.*;
import io.rsocket.loadbalance.*;
import io.rsocket.plugins.*;
import io.rsocket.transport.netty.client.*;
import io.rsocket.util.*;
import lombok.*;
import lombok.experimental.*;
import reactor.core.publisher.*;
import reactor.netty.http.client.*;
import reactor.netty.tcp.SslProvider;
import reactor.netty.tcp.*;
import static io.art.core.checker.EmptinessChecker.*;
import static io.art.core.checker.ModuleChecker.*;
import static io.art.core.checker.NullityChecker.*;
import static io.art.core.constants.StringConstants.*;
import static io.art.core.factory.ListFactory.*;
import static io.art.meta.Meta.*;
import static io.art.meta.model.TypedObject.*;
import static io.art.rsocket.constants.RsocketModuleConstants.BalancerMethod.*;
import static io.art.rsocket.constants.RsocketModuleConstants.LoggingMessages.*;
import static io.art.rsocket.constants.RsocketModuleConstants.PayloadDecoderMode.*;
import static io.art.rsocket.module.RsocketModule.*;
import static io.art.transport.extensions.TransportExtensions.*;
import static io.art.transport.mime.MimeTypeDataFormatMapper.*;
import static io.art.transport.payload.TransportPayloadWriter.*;
import static io.rsocket.core.RSocketClient.*;
import static io.rsocket.util.DefaultPayload.*;
import static java.text.MessageFormat.*;
import static java.util.Objects.*;
import static lombok.AccessLevel.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.function.*;

@UtilityClass
public class RsocketCommunicationFactory {
    @Getter(lazy = true, value = PRIVATE)
    private final static Logger logger = Logging.logger(RSOCKET_COMMUNICATOR_LOGGER);

    public static RsocketCommunication createTcpCommunication(RsocketTcpConnectorConfiguration connectorConfiguration, CommunicatorActionIdentifier identifier) {
        String connector = connectorConfiguration.getCommonConfiguration().getConnector();
        RsocketModuleConfiguration moduleConfiguration = rsocketModule().configuration();
        Supplier<RSocketClient> client = () -> createTcpClient(moduleConfiguration.getTcpConnectors().get(connector), identifier);
        return new RsocketCommunication(client, moduleConfiguration, connectorConfiguration.getCommonConfiguration());
    }

    public static RsocketCommunication createWsCommunication(RsocketWsConnectorConfiguration connectorConfiguration, CommunicatorActionIdentifier identifier) {
        String connector = connectorConfiguration.getCommonConfiguration().getConnector();
        RsocketModuleConfiguration moduleConfiguration = rsocketModule().configuration();
        Supplier<RSocketClient> client = () -> createWsClient(moduleConfiguration.getWsConnectors().get(connector), identifier);
        return new RsocketCommunication(client, moduleConfiguration, connectorConfiguration.getCommonConfiguration());
    }


    private static RSocketClient createTcpClient(RsocketTcpConnectorConfiguration connectorConfiguration, CommunicatorActionIdentifier identifier) {
        RsocketCommonConnectorConfiguration common = connectorConfiguration.getCommonConfiguration();
        RsocketSetupPayload setupPayload = createSetupPayload(common, identifier);
        ByteBuffer payloadData = transportPayloadWriter(common.getDataFormat())
                .write(typed(declaration(RsocketSetupPayload.class).definition(), setupPayload))
                .nioBuffer();
        Payload payload = DefaultPayload.create(payloadData);
        RSocketConnector connector = createConnector(common, payload);
        RsocketTcpClientGroupConfiguration group = connectorConfiguration.getGroupConfiguration();
        if (nonNull(group) && isNotEmpty(group.getClientConfigurations())) {
            return createTcpBalancer(connector, group);
        }
        return configureSocket(common, createTcpClient(common, connectorConfiguration.getSingleConfiguration(), connector), setupPayload);
    }

    private static LoadbalanceRSocketClient createTcpBalancer(RSocketConnector connector, RsocketTcpClientGroupConfiguration group) {
        List<LoadbalanceTarget> targets = linkedList();
        for (RsocketTcpClientConfiguration clientConfiguration : group.getClientConfigurations()) {
            TcpClient client = clientConfiguration.getClientDecorator().apply(TcpClient.create()
                    .host(clientConfiguration.getHost())
                    .port(clientConfiguration.getPort()));
            UnaryOperator<TcpClient> groupClientDecorator = group.getClientDecorator();
            UnaryOperator<TcpClientTransport> transportDecorator = group.getTransportDecorator();
            TcpClientTransport transport = transportDecorator.apply(TcpClientTransport.create(groupClientDecorator.apply(client), clientConfiguration.getMaxFrameLength()));
            String key = clientConfiguration.getConnector() + COLON + clientConfiguration.getHost() + COLON + clientConfiguration.getPort();
            targets.add(LoadbalanceTarget.from(key, transport));
        }
        return LoadbalanceRSocketClient.builder(Flux.just(targets))
                .loadbalanceStrategy(group.getBalancer() == ROUND_ROBIN ? new RoundRobinLoadbalanceStrategy() : WeightedLoadbalanceStrategy.builder().build())
                .connector(connector)
                .build();
    }

    private static Mono<RSocket> createTcpClient(RsocketCommonConnectorConfiguration connectorConfiguration,
                                                 RsocketTcpClientConfiguration clientConfiguration,
                                                 RSocketConnector connector) {
        UnaryOperator<TcpClient> clientDecorator = clientConfiguration.getClientDecorator();
        UnaryOperator<TcpClientTransport> transportDecorator = clientConfiguration.getTransportDecorator();
        TcpClient client = clientDecorator.apply(TcpClient.create()
                .host(clientConfiguration.getHost())
                .port(clientConfiguration.getPort()));
        RsocketSslConfiguration ssl = connectorConfiguration.getSsl();
        if (nonNull(ssl)) client.secure(createSslContext(ssl));
        return connector.connect(transportDecorator.apply(TcpClientTransport.create(client, clientConfiguration.getMaxFrameLength())));
    }

    private static RSocketClient createWsClient(RsocketWsConnectorConfiguration connectorConfiguration, CommunicatorActionIdentifier identifier) {
        RsocketCommonConnectorConfiguration common = connectorConfiguration.getCommonConfiguration();
        RsocketSetupPayload setupPayload = createSetupPayload(common, identifier);
        ByteBuffer payloadData = transportPayloadWriter(common.getDataFormat())
                .write(typed(declaration(RsocketSetupPayload.class).definition(), setupPayload))
                .nioBuffer();
        Payload payload = create(payloadData);
        RSocketConnector connector = createConnector(common, payload);
        RsocketWsClientGroupConfiguration group = connectorConfiguration.getGroupConfiguration();
        if (nonNull(group) && isNotEmpty(group.getClientConfigurations())) {
            return createWsBalancer(connector, group);
        }
        return configureSocket(common, createWsClient(common, connectorConfiguration.getSingleConfiguration(), connector), setupPayload);
    }

    private static LoadbalanceRSocketClient createWsBalancer(RSocketConnector connector, RsocketWsClientGroupConfiguration group) {
        List<LoadbalanceTarget> targets = linkedList();
        for (RsocketWsClientConfiguration clientConfiguration : group.getClientConfigurations()) {
            HttpClient client = clientConfiguration.getClientDecorator().apply(HttpClient.create()
                    .host(clientConfiguration.getHost())
                    .port(clientConfiguration.getPort()));
            UnaryOperator<WebsocketClientTransport> transportDecorator = group.getTransportDecorator();
            UnaryOperator<HttpClient> groupClientDecorator = group.getClientDecorator();
            WebsocketClientTransport transport = transportDecorator.apply(WebsocketClientTransport.create(groupClientDecorator.apply(client), clientConfiguration.getPath()));
            String key = clientConfiguration.getConnector() + COLON + clientConfiguration.getHost() + COLON + clientConfiguration.getPort();
            targets.add(LoadbalanceTarget.from(key, transport));
        }
        return LoadbalanceRSocketClient.builder(Flux.just(targets))
                .loadbalanceStrategy(group.getBalancer() == ROUND_ROBIN ? new RoundRobinLoadbalanceStrategy() : WeightedLoadbalanceStrategy.builder().build())
                .connector(connector)
                .build();
    }

    private static Mono<RSocket> createWsClient(RsocketCommonConnectorConfiguration connectorConfiguration,
                                                RsocketWsClientConfiguration clientConfiguration,
                                                RSocketConnector connector) {
        UnaryOperator<HttpClient> clientDecorator = clientConfiguration.getClientDecorator();
        UnaryOperator<WebsocketClientTransport> transportDecorator = clientConfiguration.getTransportDecorator();
        HttpClient client = clientDecorator.apply(HttpClient.create()
                .host(clientConfiguration.getHost())
                .port(clientConfiguration.getPort()));
        RsocketSslConfiguration ssl = connectorConfiguration.getSsl();
        if (nonNull(ssl)) client.secure(createSslContext(ssl));
        return connector.connect(transportDecorator.apply(WebsocketClientTransport.create(client, clientConfiguration.getPath())));
    }

    private static SslProvider createSslContext(RsocketSslConfiguration ssl) {
        try {
            File certificate = ssl.getCertificate();
            File key = ssl.getKey();
            SslContextBuilder sslBuilder = SslContextBuilder.forClient();
            if (nonNull(key) && key.exists()) {
                sslBuilder.keyManager(nonNull(certificate) && certificate.exists() ? certificate : null, key);
            }
            String password = ssl.getPassword();
            if (isNotEmpty(password)) {
                sslBuilder.keyManager(
                        nonNull(certificate) && certificate.exists() ? certificate : null,
                        nonNull(key) && key.exists() ? key : null,
                        password);
            }
            return SslProvider.builder().sslContext(sslBuilder.build()).build();
        } catch (Throwable throwable) {
            throw new RsocketException(throwable);
        }
    }

    private static RsocketSetupPayload createSetupPayload(RsocketCommonConnectorConfiguration common, CommunicatorActionIdentifier identifier) {
        ServiceMethodIdentifier targetServiceMethod = common.getService().id(identifier);
        RsocketSetupPayloadBuilder payloadBuilder = RsocketSetupPayload.builder()
                .dataFormat(common.getDataFormat())
                .metadataFormat(common.getMetaDataFormat());
        if (nonNull(targetServiceMethod)) {
            payloadBuilder
                    .serviceId(targetServiceMethod.getServiceId())
                    .methodId(targetServiceMethod.getMethodId());
        }
        return payloadBuilder.build();
    }

    private static RSocketClient configureSocket(RsocketCommonConnectorConfiguration common, Mono<RSocket> socket, RsocketSetupPayload setupPayload) {
        Mono<RSocket> configured = socket.timeout(common.getTimeout());
        if (withLogging() && common.isVerbose()) {
            configured = configured
                    .doOnSubscribe(subscription -> getLogger().info(format(RSOCKET_COMMUNICATOR_STARTED, common.getConnector(), toPrettyString(setupPayload))))
                    .doOnError(throwable -> getLogger().error(throwable.getMessage(), throwable));
        }
        return from(configured);
    }

    private static RSocketConnector createConnector(RsocketCommonConnectorConfiguration commonConfiguration, Payload payload) {
        RSocketConnector connector = RSocketConnector.create()
                .payloadDecoder(commonConfiguration.getPayloadDecoderMode() == ZERO_COPY ? PayloadDecoder.ZERO_COPY : PayloadDecoder.DEFAULT)
                .dataMimeType(toMimeType(commonConfiguration.getDataFormat()).toString())
                .metadataMimeType(toMimeType(commonConfiguration.getMetaDataFormat()).toString())
                .fragment(commonConfiguration.getFragment())
                .interceptors(registry -> configureInterceptors(commonConfiguration, registry));
        apply(commonConfiguration.getKeepAlive(), keepAlive -> connector.keepAlive(keepAlive.getInterval(), keepAlive.getMaxLifeTime()));
        apply(commonConfiguration.getResume(), resume -> connector.resume(resume.toResume()));
        apply(commonConfiguration.getRetry(), retry -> connector.reconnect(retry.toRetry()));
        return commonConfiguration.getDecorator().apply(connector.setupPayload(payload));
    }

    private static void configureInterceptors(RsocketCommonConnectorConfiguration connectorConfiguration, InterceptorRegistry registry) {
        UnaryOperator<InterceptorRegistry> interceptors = connectorConfiguration.getInterceptors();
        if (withLogging()) {
            interceptors.apply(registry
                    .forResponder(new RsocketConnectorLoggingInterceptor(rsocketModule().configuration(), connectorConfiguration))
                    .forRequester(new RsocketConnectorLoggingInterceptor(rsocketModule().configuration(), connectorConfiguration)));
        }
    }
}
