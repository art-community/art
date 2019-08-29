/*
 * ART Java
 *
 * Copyright 2019 ART
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

package ru.art.rsocket.configuration;

import io.rsocket.plugins.*;
import lombok.*;
import ru.art.core.module.*;
import ru.art.entity.*;
import ru.art.entity.interceptor.*;
import ru.art.logging.*;
import ru.art.rsocket.constants.RsocketModuleConstants.*;
import ru.art.rsocket.exception.*;
import ru.art.rsocket.interceptor.*;
import ru.art.rsocket.model.*;
import static java.text.MessageFormat.*;
import static ru.art.core.constants.NetworkConstants.*;
import static ru.art.core.extension.ExceptionExtensions.*;
import static ru.art.core.factory.CollectionsFactory.*;
import static ru.art.core.network.selector.PortSelector.*;
import static ru.art.rsocket.constants.RsocketModuleConstants.*;
import static ru.art.rsocket.constants.RsocketModuleConstants.RsocketDataFormat.*;
import java.util.*;

public interface RsocketModuleConfiguration extends ModuleConfiguration {
    String getServerHost();

    int getServerTcpPort();

    int getServerWebSocketPort();

    String getBalancerHost();

    int getBalancerTcpPort();

    int getBalancerWebSocketPort();

    boolean isResumableServer();

    long getServerResumeSessionDuration();

    boolean isResumableClient();

    long getClientResumeSessionDuration();

    RsocketDataFormat getDataFormat();

    Map<String, RsocketCommunicationTargetConfiguration> getCommunicationTargets();

    boolean isEnableRawDataTracing();

    boolean isEnableValueTracing();

    List<RSocketInterceptor> getClientInterceptors();

    List<RSocketInterceptor> getServerInterceptors();

    default RsocketCommunicationTargetConfiguration getCommunicationTargetConfiguration(String serviceId) {
        return exceptionIfNull(getCommunicationTargets().get(serviceId),
                new RsocketClientException(format(RSOCKET_COMMUNICATION_TARGET_CONFIGURATION_NOT_FOUND, serviceId)))
                .toBuilder()
                .build();
    }

    RsocketModuleDefaultConfiguration DEFAULT_CONFIGURATION = new RsocketModuleDefaultConfiguration();

    List<ValueInterceptor<Entity, Entity>> getRequestValueInterceptors();

    List<ValueInterceptor<Entity, Entity>> getResponseValueInterceptors();

    @Getter
    class RsocketModuleDefaultConfiguration implements RsocketModuleConfiguration {
        private final RsocketDataFormat dataFormat = PROTOBUF;
        private final String serverHost = BROADCAST_IP_ADDRESS;
        private final int serverTcpPort = findAvailableTcpPort();
        private final int serverWebSocketPort = findAvailableTcpPort();
        private final String balancerHost = LOCALHOST;
        private final int balancerTcpPort = DEFAULT_RSOCKET_TCP_PORT;
        private final int balancerWebSocketPort = DEFAULT_RSOCKET_WEB_SOCKET_PORT;
        private final boolean resumableServer = true;
        private final long serverResumeSessionDuration = DEFAULT_RSOCKET_RESUME_SESSION_DURATION;
        private final boolean resumableClient = true;
        private final long clientResumeSessionDuration = DEFAULT_RSOCKET_RESUME_SESSION_DURATION;
        private final boolean enableRawDataTracing = false;
        private final boolean enableValueTracing = false;
        @Getter(lazy = true, onMethod = @__({@SuppressWarnings("unchecked")}))
        private final List<RSocketInterceptor> serverInterceptors = initializeInterceptors();
        @Getter(lazy = true, onMethod = @__({@SuppressWarnings("unchecked")}))
        private final List<RSocketInterceptor> clientInterceptors = initializeInterceptors();
        @Getter(lazy = true, onMethod = @__({@SuppressWarnings("unchecked")}))
        private final List<ValueInterceptor<Entity, Entity>> requestValueInterceptors = initializeValueInterceptors();
        @Getter(lazy = true, onMethod = @__({@SuppressWarnings("unchecked")}))
        private final List<ValueInterceptor<Entity, Entity>> responseValueInterceptors = initializeValueInterceptors();

        private List<RSocketInterceptor> initializeInterceptors() {
            return linkedListOf(new RsocketLoggingInterceptor(this::isEnableRawDataTracing));
        }

        private List<ValueInterceptor<Entity, Entity>> initializeValueInterceptors() {
            return isEnableValueTracing() ? linkedListOf(new LoggingValueInterceptor<>()) : linkedListOf();
        }

        private final Map<String, RsocketCommunicationTargetConfiguration> communicationTargets = mapOf();
    }
}
