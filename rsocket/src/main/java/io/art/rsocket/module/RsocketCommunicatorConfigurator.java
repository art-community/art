package io.art.rsocket.module;

import io.art.communicator.*;
import io.art.communicator.configurator.*;
import io.art.core.annotation.*;
import io.art.core.collection.*;
import io.art.rsocket.configuration.communicator.tcp.*;
import io.art.rsocket.configuration.communicator.ws.*;
import static io.art.core.factory.MapFactory.*;
import static io.art.core.normalizer.ClassIdentifierNormalizer.*;
import static io.art.rsocket.communicator.RsocketCommunicationFactory.*;
import static java.util.function.UnaryOperator.*;
import java.util.*;
import java.util.function.*;

@Public
public class RsocketCommunicatorConfigurator extends CommunicatorConfigurator<RsocketCommunicatorConfigurator> {
    private final Map<String, RsocketTcpConnectorConfiguration> tcpConnectors = map();
    private final Map<String, RsocketWsConnectorConfiguration> wsConnectors = map();

    public RsocketCommunicatorConfigurator tcp(Class<? extends Communicator> communicatorClass) {
        return tcp(() -> idByDash(communicatorClass), communicatorClass, identity());
    }

    public RsocketCommunicatorConfigurator ws(Class<? extends Communicator> communicatorClass) {
        return ws(() -> idByDash(communicatorClass), communicatorClass, identity());
    }

    public RsocketCommunicatorConfigurator tcp(ConnectorIdentifier connector, Class<? extends Communicator> communicatorClass) {
        return tcp(connector, communicatorClass, identity());
    }

    public RsocketCommunicatorConfigurator ws(ConnectorIdentifier connector, Class<? extends Communicator> communicatorClass) {
        return ws(connector, communicatorClass, identity());
    }

    public RsocketCommunicatorConfigurator tcp(Class<? extends Communicator> communicatorClass, UnaryOperator<RsocketTcpConnectorConfigurator> configurator) {
        return tcp(() -> idByDash(communicatorClass), communicatorClass, configurator);
    }

    public RsocketCommunicatorConfigurator ws(Class<? extends Communicator> communicatorClass, UnaryOperator<RsocketWsConnectorConfigurator> configurator) {
        return ws(() -> idByDash(communicatorClass), communicatorClass, configurator);
    }

    public RsocketCommunicatorConfigurator tcp(ConnectorIdentifier connector,
                                               Class<? extends Communicator> communicatorClass,
                                               UnaryOperator<RsocketTcpConnectorConfigurator> configurator) {
        RsocketTcpConnectorConfigurator connectorConfigurator = configurator.apply(new RsocketTcpConnectorConfigurator(connector.id()));
        RsocketTcpConnectorConfiguration configuration = connectorConfigurator.configure();
        tcpConnectors.put(connector.id(), configuration);
        register(communicatorClass, identifier -> createManagedTcpCommunication(configuration, identifier));
        return this;
    }

    public RsocketCommunicatorConfigurator ws(ConnectorIdentifier connector,
                                              Class<? extends Communicator> communicatorClass,
                                              UnaryOperator<RsocketWsConnectorConfigurator> configurator) {
        RsocketWsConnectorConfigurator connectorConfigurator = configurator.apply(new RsocketWsConnectorConfigurator(connector.id()));
        RsocketWsConnectorConfiguration configuration = connectorConfigurator.configure();
        wsConnectors.put(connector.id(), configuration);
        register(communicatorClass, identifier -> createManagedWsCommunication(configuration, identifier));
        return this;
    }

    ImmutableMap<String, RsocketTcpConnectorConfiguration> tcpConnectors() {
        return immutableMapOf(tcpConnectors);
    }

    ImmutableMap<String, RsocketWsConnectorConfiguration> wsConnectors() {
        return immutableMapOf(wsConnectors);
    }
}
