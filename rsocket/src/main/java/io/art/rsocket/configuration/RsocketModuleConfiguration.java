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

package io.art.rsocket.configuration;

import io.art.communicator.configuration.*;
import io.art.communicator.model.*;
import io.art.communicator.refresher.*;
import io.art.core.collection.*;
import io.art.core.module.*;
import io.art.core.property.*;
import io.art.core.source.*;
import io.art.rsocket.configuration.communicator.http.*;
import io.art.rsocket.configuration.communicator.tcp.*;
import io.art.rsocket.configuration.server.*;
import io.art.rsocket.refresher.*;
import io.art.server.configuration.*;
import io.art.server.method.*;
import io.art.server.refresher.*;
import lombok.*;
import static io.art.core.checker.NullityChecker.*;
import static io.art.core.collection.ImmutableMap.*;
import static io.art.core.extensions.CollectionExtensions.*;
import static io.art.core.property.LazyProperty.*;
import static io.art.rsocket.constants.RsocketModuleConstants.ConfigurationKeys.*;
import static java.util.Optional.*;
import java.util.*;

@RequiredArgsConstructor
public class RsocketModuleConfiguration implements ModuleConfiguration {
    private final RsocketModuleRefresher refresher;
    private final ServerRefresher serverRefresher;
    private final CommunicatorRefresher communicatorRefresher;

    @Getter
    private RsocketModuleRefresher.Consumer consumer;

    @Getter
    private ServerConfiguration server;

    @Getter
    private boolean enableTcpServer;

    @Getter
    private boolean enableHttpServer;

    @Getter
    private RsocketTcpServerConfiguration tcpServer;

    @Getter
    private RsocketHttpServerConfiguration httpServer;

    @Getter
    private LazyProperty<ImmutableArray<ServiceMethod>> serviceMethods;


    @Getter
    private LazyProperty<ImmutableMap<Class<? extends Connector>, ? extends Connector>> connectors;

    @Getter
    private LazyProperty<ImmutableMap<Class<? extends Connector>, ? extends Communicator>> communicators;

    @Getter
    private CommunicatorConfiguration communicator;

    @Getter
    private ImmutableMap<String, RsocketTcpConnectorConfiguration> tcpConnector;

    @Getter
    private ImmutableMap<String, RsocketHttpConnectorConfiguration> httpConnector;


    public RsocketModuleConfiguration(RsocketModuleRefresher refresher) {
        this.refresher = refresher;
        serverRefresher = new ServerRefresher();
        communicatorRefresher = new CommunicatorRefresher();
        consumer = refresher.consumer();

        server = ServerConfiguration.defaults(serverRefresher);
        tcpServer = RsocketTcpServerConfiguration.defaults();
        httpServer = RsocketHttpServerConfiguration.defaults();
        serviceMethods = lazy(ImmutableArray::emptyImmutableArray);
        enableTcpServer = false;
        enableHttpServer = false;

        communicator = CommunicatorConfiguration.defaults(communicatorRefresher);
        tcpConnector = emptyImmutableMap();
        httpConnector = emptyImmutableMap();
        connectors = lazy(ImmutableMap::emptyImmutableMap);
        communicators = lazy(ImmutableMap::emptyImmutableMap);
    }

    @RequiredArgsConstructor
    public static class Configurator implements ModuleConfigurator<RsocketModuleConfiguration, Configurator> {
        private final RsocketModuleConfiguration configuration;

        @Override
        public Configurator from(ConfigurationSource source) {
            Optional<NestedConfiguration> serverSection = ofNullable(source.getNested(RSOCKET_SECTION))
                    .map(rsocket -> rsocket.getNested(SERVER_SECTION));
            serverSection
                    .map(this::tcpServer)
                    .ifPresent(serverConfiguration -> configuration.tcpServer = serverConfiguration);
            serverSection
                    .map(this::httpServer)
                    .ifPresent(serverConfiguration -> configuration.httpServer = serverConfiguration);
            serverSection
                    .map(this::server)
                    .ifPresent(serverConfiguration -> configuration.server = serverConfiguration);

            Optional<NestedConfiguration> communicatorSection = ofNullable(source.getNested(RSOCKET_SECTION))
                    .map(rsocket -> rsocket.getNested(COMMUNICATOR_SECTION));
            communicatorSection
                    .map(this::tcpConnectors)
                    .ifPresent(communicatorConfiguration -> configuration.tcpConnector = merge(configuration.tcpConnector, communicatorConfiguration));
            communicatorSection
                    .map(this::httpConnectors)
                    .ifPresent(communicatorConfiguration -> configuration.httpConnector = merge(configuration.httpConnector, communicatorConfiguration));
            communicatorSection
                    .map(this::communicator)
                    .ifPresent(communicatorConfiguration -> configuration.communicator = communicatorConfiguration);

            configuration.refresher.produce();
            configuration.serverRefresher.produce();
            configuration.communicatorRefresher.produce();
            return this;
        }

        private ServerConfiguration server(NestedConfiguration server) {
            return ServerConfiguration.from(configuration.serverRefresher, server);
        }

        private RsocketHttpServerConfiguration httpServer(NestedConfiguration server) {
            return RsocketHttpServerConfiguration.from(configuration.refresher, configuration.httpServer, server);
        }

        private RsocketTcpServerConfiguration tcpServer(NestedConfiguration server) {
            return RsocketTcpServerConfiguration.from(configuration.refresher, configuration.tcpServer, server);
        }


        private CommunicatorConfiguration communicator(NestedConfiguration communicator) {
            return CommunicatorConfiguration.from(configuration.communicatorRefresher, communicator);
        }

        private ImmutableMap<String, RsocketHttpConnectorConfiguration> httpConnectors(NestedConfiguration communicator) {
            return communicator.getNestedMap(CONNECTORS_KEY, nested -> let(
                    configuration.httpConnector.get(nested.getSection()),
                    current -> httpConnector(nested, current),
                    httpConnector(nested))
            );
        }

        private RsocketHttpConnectorConfiguration httpConnector(NestedConfiguration nested) {
            return RsocketHttpConnectorConfiguration.from(configuration.refresher, nested);
        }

        private RsocketHttpConnectorConfiguration httpConnector(NestedConfiguration nested, RsocketHttpConnectorConfiguration current) {
            return RsocketHttpConnectorConfiguration.from(configuration.refresher, current, nested);
        }

        private ImmutableMap<String, RsocketTcpConnectorConfiguration> tcpConnectors(NestedConfiguration communicator) {
            return communicator.getNestedMap(CONNECTORS_KEY, nested -> let(configuration.tcpConnector.get(nested.getSection()),
                    current -> tcpConnector(nested, current),
                    tcpConnector(nested)
            ));
        }

        private RsocketTcpConnectorConfiguration tcpConnector(NestedConfiguration nested) {
            return RsocketTcpConnectorConfiguration.from(configuration.refresher, nested);
        }

        private RsocketTcpConnectorConfiguration tcpConnector(NestedConfiguration nested, RsocketTcpConnectorConfiguration current) {
            return RsocketTcpConnectorConfiguration.from(configuration.refresher, current, nested);
        }


        @Override
        public Configurator initialize(RsocketModuleConfiguration configuration) {
            this.configuration.enableTcpServer = configuration.isEnableTcpServer();
            this.configuration.enableHttpServer = configuration.isEnableHttpServer();

            this.configuration.server = configuration.getServer();
            this.configuration.tcpServer = configuration.getTcpServer();
            this.configuration.httpServer = configuration.getHttpServer();
            this.configuration.serviceMethods = configuration.getServiceMethods();

            this.configuration.httpConnector = configuration.getHttpConnector();
            this.configuration.tcpConnector = configuration.getTcpConnector();
            this.configuration.communicator = configuration.getCommunicator();
            this.configuration.connectors = configuration.getConnectors();
            this.configuration.communicators = configuration.getCommunicators();
            return this;
        }
    }

}
