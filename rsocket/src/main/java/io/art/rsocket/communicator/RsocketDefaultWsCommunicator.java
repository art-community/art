package io.art.rsocket.communicator;

import io.art.communicator.action.*;
import io.art.communicator.model.*;
import io.art.core.model.*;
import io.art.core.property.*;
import io.art.core.strategy.*;
import io.art.meta.*;
import io.art.rsocket.configuration.common.*;
import io.art.rsocket.configuration.communicator.ws.*;
import io.art.rsocket.constants.RsocketModuleConstants.*;
import io.art.rsocket.meta.MetaRsocket.MetaIoPackage.MetaArtPackage.MetaRsocketPackage.MetaPortalPackage.MetaRsocketDefaultPortalClass.*;
import io.art.rsocket.portal.RsocketDefaultPortal.*;
import io.art.transport.constants.TransportModuleConstants.*;
import io.rsocket.core.*;
import io.rsocket.plugins.*;
import io.rsocket.transport.netty.client.*;
import reactor.core.publisher.*;
import reactor.netty.http.client.*;
import static io.art.communicator.factory.CommunicatorProxyFactory.*;
import static io.art.core.checker.NullityChecker.*;
import static io.art.core.extensions.CollectionExtensions.*;
import static io.art.core.factory.SetFactory.*;
import static io.art.core.model.ServiceMethodIdentifier.*;
import static io.art.core.normalizer.ClassIdentifierNormalizer.*;
import static io.art.core.property.LazyProperty.*;
import static io.art.rsocket.communicator.RsocketCommunicationFactory.*;
import static io.art.rsocket.configuration.communicator.common.RsocketCommonConnectorConfiguration.*;
import static io.art.rsocket.configuration.communicator.ws.RsocketWsConnectorConfiguration.*;
import static io.art.rsocket.constants.RsocketModuleConstants.Defaults.*;
import java.time.*;
import java.util.function.*;

public class RsocketDefaultWsCommunicator implements RsocketDefaultCommunicator {
    private final RsocketCommonConnectorConfigurationBuilder commonConnector = commonConnectorConfiguration(DEFAULT_CONNECTOR_ID).toBuilder();
    private final RsocketWsConnectorConfigurationBuilder wsConnector = wsConnectorConfiguration(DEFAULT_CONNECTOR_ID).toBuilder();
    private final static LazyProperty<MetaRsocketExecutionCommunicatorClass> communicatorClass = lazy(() -> Meta.declaration(RsocketExecutionCommunicator.class));
    private volatile CommunicatorProxy<RsocketExecutionCommunicator> proxy;
    private ServiceMethodIdentifier serviceMethodId;

    public RsocketDefaultWsCommunicator verbose(boolean value) {
        commonConnector.verbose(value);
        return this;
    }

    public RsocketDefaultWsCommunicator fragment(int value) {
        commonConnector.fragment(value);
        return this;
    }

    public RsocketDefaultWsCommunicator keepAlive(RsocketKeepAliveConfiguration value) {
        commonConnector.keepAlive(value);
        return this;
    }

    public RsocketDefaultWsCommunicator resume(RsocketResumeConfiguration value) {
        commonConnector.resume(value);
        return this;
    }

    public RsocketDefaultWsCommunicator retry(RsocketRetryConfiguration value) {
        commonConnector.retry(value);
        return this;
    }

    public RsocketDefaultWsCommunicator payloadDecoderMode(PayloadDecoderMode value) {
        commonConnector.payloadDecoderMode(value);
        return this;
    }

    public RsocketDefaultWsCommunicator maxInboundPayloadSize(int value) {
        commonConnector.maxInboundPayloadSize(value);
        return this;
    }

    public RsocketDefaultWsCommunicator service(Class<?> value) {
        commonConnector.service(ServiceMethodStrategy.manual(value));
        return this;
    }

    public RsocketDefaultWsCommunicator timeout(Duration value) {
        commonConnector.timeout(value);
        return this;
    }

    public RsocketDefaultWsCommunicator target(String serviceId, String methodId) {
        serviceMethodId = serviceMethodId(serviceId, methodId);
        return this;
    }

    public RsocketDefaultWsCommunicator target(Class<?> serviceId, String methodId) {
        serviceMethodId = serviceMethodId(idByDash(serviceId), methodId);
        return this;
    }

    public RsocketDefaultWsCommunicator interceptors(UnaryOperator<InterceptorRegistry> interceptors) {
        commonConnector.interceptors(interceptors);
        return this;
    }

    public RsocketDefaultWsCommunicator connectorDecorator(UnaryOperator<RSocketConnector> decorator) {
        commonConnector.decorator(decorator);
        return this;
    }

    public RsocketDefaultWsCommunicator ssl(RsocketSslConfiguration ssl) {
        commonConnector.ssl(ssl);
        return this;
    }

    public RsocketDefaultWsCommunicator balancer(BalancerMethod value) {
        wsConnector.balancer(value);
        return this;
    }

    public RsocketDefaultWsCommunicator clientDecorator(UnaryOperator<HttpClient> clientDecorator) {
        wsConnector.clientDecorator(clientDecorator);
        return this;
    }

    public RsocketDefaultWsCommunicator transportDecorator(UnaryOperator<WebsocketClientTransport> transportDecorator) {
        wsConnector.transportDecorator(transportDecorator);
        return this;
    }

    public RsocketDefaultWsCommunicator client(String host, int port, String path) {
        RsocketWsClientConfiguration clientConfiguration = RsocketWsClientConfiguration.builder()
                .host(host)
                .port(port)
                .path(path)
                .connector(DEFAULT_CONNECTOR_ID)
                .build();
        wsConnector.clientConfigurations(immutableSetOf(addToSet(wsConnector.build().getClientConfigurations().toMutable(), clientConfiguration)));
        return this;
    }


    public void fireAndForget(DataFormat dataFormat) {
        RsocketWsConnectorConfiguration configuration = wsConnector
                .commonConfiguration(commonConnector.dataFormat(dataFormat).build())
                .build();
        createCommunicator(configuration).fireAndForget(Mono.empty());
    }

    public void fireAndForget(RsocketDefaultRequest request) {
        RsocketWsConnectorConfiguration configuration = wsConnector
                .commonConfiguration(commonConnector.dataFormat(request.getDataFormat()).build())
                .build();
        createCommunicator(configuration).fireAndForget(Mono.just(request.getInput()));
    }

    public void fireAndForget(RsocketMonoRequest request) {
        RsocketWsConnectorConfiguration configuration = wsConnector
                .commonConfiguration(commonConnector.dataFormat(request.getDataFormat()).build())
                .build();
        createCommunicator(configuration).fireAndForget(request.getInput());
    }


    public RsocketDefaultResponse requestResponse(DataFormat dataFormat) {
        RsocketWsConnectorConfiguration configuration = wsConnector
                .commonConfiguration(commonConnector.dataFormat(dataFormat).build())
                .build();
        return new RsocketDefaultResponse(this, createCommunicator(configuration).requestResponse(Mono.empty()).flux(), dataFormat);
    }

    public RsocketDefaultResponse requestResponse(RsocketDefaultRequest request) {
        RsocketWsConnectorConfiguration configuration = wsConnector
                .commonConfiguration(commonConnector.dataFormat(request.getDataFormat()).build())
                .build();
        return new RsocketDefaultResponse(this, createCommunicator(configuration).requestResponse(Mono.just(request.getInput())).flux(), request.getDataFormat());
    }

    public RsocketDefaultResponse requestResponse(RsocketMonoRequest request) {
        RsocketWsConnectorConfiguration configuration = wsConnector
                .commonConfiguration(commonConnector.dataFormat(request.getDataFormat()).build())
                .build();
        return new RsocketDefaultResponse(this, createCommunicator(configuration).requestResponse(request.getInput()).flux(), request.getDataFormat());
    }


    public RsocketReactiveResponse requestStream(DataFormat dataFormat) {
        RsocketWsConnectorConfiguration configuration = wsConnector
                .commonConfiguration(commonConnector.dataFormat(dataFormat).build())
                .build();
        return new RsocketReactiveResponse(this, createCommunicator(configuration).requestStream(Mono.empty()), dataFormat);
    }

    public RsocketReactiveResponse requestStream(RsocketDefaultRequest request) {
        RsocketWsConnectorConfiguration configuration = wsConnector
                .commonConfiguration(commonConnector.dataFormat(request.getDataFormat()).build())
                .build();
        return new RsocketReactiveResponse(this, createCommunicator(configuration).requestStream(Mono.just(request.getInput())), request.getDataFormat());
    }

    public RsocketReactiveResponse requestStream(RsocketMonoRequest request) {
        RsocketWsConnectorConfiguration configuration = wsConnector
                .commonConfiguration(commonConnector.dataFormat(request.getDataFormat()).build())
                .build();
        return new RsocketReactiveResponse(this, createCommunicator(configuration).requestStream(request.getInput()), request.getDataFormat());
    }


    public RsocketReactiveResponse requestChannel(DataFormat dataFormat) {
        RsocketWsConnectorConfiguration configuration = wsConnector
                .commonConfiguration(commonConnector.dataFormat(dataFormat).build())
                .build();
        return new RsocketReactiveResponse(this, createCommunicator(configuration).requestChannel(Flux.empty()), dataFormat);
    }

    public RsocketReactiveResponse requestChannel(RsocketFluxRequest request) {
        RsocketWsConnectorConfiguration configuration = wsConnector
                .commonConfiguration(commonConnector.dataFormat(request.getDataFormat()).build())
                .build();
        return new RsocketReactiveResponse(this, createCommunicator(configuration).requestChannel(request.getInput()), request.getDataFormat());
    }


    @Override
    public void dispose() {
        apply(proxy, proxy -> proxy.getActions().values().forEach(CommunicatorAction::dispose));
    }

    private RsocketExecutionCommunicator createCommunicator(RsocketWsConnectorConfiguration connector) {
        return (proxy = communicatorProxy(communicatorClass.get(), () -> createCommunication(connector))).getCommunicator();
    }

    private RsocketCommunication createCommunication(RsocketWsConnectorConfiguration connector) {
        return createDefaultWsCommunication(connector, createSetupPayload(connector.getCommonConfiguration(), serviceMethodId));
    }
}
