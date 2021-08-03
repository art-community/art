package io.art.rsocket.module;

import io.art.communicator.configuration.*;
import io.art.communicator.configurator.*;
import io.art.communicator.model.*;
import io.art.core.collection.*;
import io.art.rsocket.*;
import io.art.rsocket.configuration.communicator.http.*;
import io.art.rsocket.configuration.communicator.tcp.*;
import lombok.*;
import lombok.experimental.*;
import static io.art.core.caster.Caster.*;
import static io.art.core.factory.MapFactory.*;
import static io.art.core.normalizer.ClassIdentifierNormalizer.*;
import java.util.*;
import java.util.function.*;

@Setter
@Accessors(fluent = true)
public class RsocketCommunicatorConfigurator extends CommunicatorConfigurator {
    private Map<String, RsocketTcpConnectorConfiguration> tcpConnectors = map();
    private Map<String, RsocketHttpConnectorConfiguration> httpConnectors = map();

    public RsocketCommunicatorConfigurator tcp(Class<? extends Connector> connectorClass) {
        return tcp(connectorClass, cast(Function.identity()));
    }

    public RsocketCommunicatorConfigurator http(Class<? extends Connector> connectorClass) {
        return http(connectorClass, UnaryOperator.identity());
    }

    public RsocketCommunicatorConfigurator tcp(Class<? extends Connector> connectorClass, UnaryOperator<RsocketTcpConnectorConfigurator> configurator) {
        RsocketTcpConnectorConfigurator connectorConfigurator = configurator.apply(new RsocketTcpConnectorConfigurator(asId(connectorClass)));
        registerConnector(connectorClass, Rsocket::rsocketCommunicator);
        tcpConnectors.put(asId(connectorClass), connectorConfigurator.configure());
        return this;
    }

    public RsocketCommunicatorConfigurator http(Class<? extends Connector> connectorClass, UnaryOperator<RsocketHttpConnectorConfigurator> configurator) {
        RsocketHttpConnectorConfigurator connectorConfigurator = configurator.apply(new RsocketHttpConnectorConfigurator(asId(connectorClass)));
        registerConnector(connectorClass, Rsocket::rsocketCommunicator);
        httpConnectors.put(asId(connectorClass), connectorConfigurator.configure());
        return this;
    }

    ImmutableMap<String, RsocketTcpConnectorConfiguration> configureTcp() {
        return immutableMapOf(tcpConnectors);
    }

    ImmutableMap<String, RsocketHttpConnectorConfiguration> configureHttp() {
        return immutableMapOf(httpConnectors);
    }

    CommunicatorConfiguration configureCommunicator(CommunicatorConfiguration current) {
        return configure(current.toBuilder().build());
    }
}
