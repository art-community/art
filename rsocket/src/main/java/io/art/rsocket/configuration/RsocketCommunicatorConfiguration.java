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

import io.art.core.collection.*;
import io.art.core.source.*;
import io.art.rsocket.refresher.*;
import lombok.*;
import static io.art.core.checker.NullityChecker.*;
import static io.art.rsocket.configuration.RsocketCommonConnectorConfiguration.*;
import static io.art.rsocket.constants.RsocketModuleConstants.ConfigurationKeys.*;
import static java.util.Optional.*;

@Getter
public class RsocketCommunicatorConfiguration {
    private RsocketCommonConnectorConfiguration defaultConnectorConfiguration;
    private ImmutableMap<String, RsocketCommonConnectorConfiguration> connectorConfigurations;

    public boolean isLogging(String connectorId) {
        return ofNullable(connectorConfigurations)
                .map(configurations -> configurations.get(connectorId))
                .map(RsocketCommonConnectorConfiguration::isLogging)
                .orElseGet(() -> let(defaultConnectorConfiguration, RsocketCommonConnectorConfiguration::isLogging, false));
    }

    public static RsocketCommunicatorConfiguration from(RsocketModuleRefresher refresher, ConfigurationSource source) {
        RsocketCommunicatorConfiguration configuration = new RsocketCommunicatorConfiguration();
        configuration.defaultConnectorConfiguration = let(source.getNested(DEFAULT_SECTION), RsocketCommonConnectorConfiguration::from, defaults());
        configuration.connectorConfigurations = source.getNestedMap(CONNECTORS_KEY, connector -> RsocketCommonConnectorConfiguration.from(refresher, configuration.defaultConnectorConfiguration, connector));
        return configuration;
    }
}
