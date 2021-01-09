/*
 * ART
 *
 * Copyright 2020 ART
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

package io.art.model.configurator;

import io.art.communicator.action.CommunicatorAction.*;
import io.art.model.implementation.communicator.*;
import lombok.*;
import static lombok.AccessLevel.*;
import java.util.function.*;

@Getter(value = PACKAGE)
@RequiredArgsConstructor(access = PACKAGE)
public class RsocketCommunicatorModelConfigurator {
    private final String id;
    private final Class<?> proxyClass;
    private final Function<CommunicatorActionBuilder, CommunicatorActionBuilder> decorator;
    private String targetServiceId;
    private String targetMethodId;

    public RsocketCommunicatorModelConfigurator to(Class<?> targetService) {
        return to(targetService.getSimpleName());
    }

    public RsocketCommunicatorModelConfigurator to(String targetServiceId) {
        this.targetServiceId = targetServiceId;
        return this;
    }

    public RsocketCommunicatorModelConfigurator overrideMethod(String id) {
        this.targetMethodId = id;
        return this;
    }

    RsocketCommunicatorModel configure() {
        return RsocketCommunicatorModel.builder()
                .id(id)
                .proxyClass(proxyClass)
                .targetServiceId(targetServiceId)
                .targetMethodId(targetMethodId)
                .decorator(decorator)
                .build();
    }
}
