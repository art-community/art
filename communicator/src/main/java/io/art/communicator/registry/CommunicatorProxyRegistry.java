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

package io.art.communicator.registry;

import io.art.communicator.action.*;
import io.art.communicator.constants.CommunicatorModuleConstants.*;
import io.art.communicator.proxy.*;
import io.art.core.collection.*;
import io.art.core.model.*;
import static io.art.core.collection.ImmutableMap.*;
import static io.art.core.factory.MapFactory.*;
import static java.util.Optional.*;
import java.util.*;

public class CommunicatorProxyRegistry {
    private final Map<String, CommunicatorProxy> proxies = map();

    public Optional<CommunicatorProxy> get(String id) {
        return ofNullable(proxies.get(id));
    }

    public Set<String> identifiers() {
        return proxies.keySet();
    }

    public ImmutableMap<String, CommunicatorProxy> getByProtocol(CommunicatorProtocol protocol) {
        return proxies
                .entrySet()
                .stream()
                .filter(entry -> protocol.equals(entry.getValue().getProtocol()))
                .collect(immutableMapCollector(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Optional<CommunicatorAction> findActionById(CommunicatorActionIdentifier identifier) {
        return get(identifier.getCommunicatorId()).map(proxy -> proxy.getActions().get(identifier.getActionId()));
    }

    public CommunicatorProxyRegistry register(String id, CommunicatorProxy proxy) {
        proxies.put(id, proxy);
        return this;
    }

    public ImmutableMap<String, CommunicatorProxy> getProxies() {
        return immutableMapOf(proxies);
    }
}
