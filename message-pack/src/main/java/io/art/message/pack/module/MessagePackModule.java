/*
 * ART
 *
 * Copyright 2019-2022 ART
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

package io.art.message.pack.module;

import io.art.core.module.*;
import io.art.core.property.*;
import io.art.message.pack.configuration.*;
import lombok.*;
import static io.art.core.constants.ModuleIdentifiers.*;
import static io.art.core.context.Context.*;
import static io.art.core.property.LazyProperty.*;

@Getter
public class MessagePackModule implements StatelessModule<MessagePackModuleConfiguration, MessagePackModuleConfiguration.Configurator> {
    private static final LazyProperty<StatelessModuleProxy<MessagePackModuleConfiguration>> messagePackModule = lazy(() -> context().getStatelessModule(MESSAGE_PACK_MODULE_ID));
    private final String id = MESSAGE_PACK_MODULE_ID;
    private final MessagePackModuleConfiguration configuration = new MessagePackModuleConfiguration();
    private final MessagePackModuleConfiguration.Configurator configurator = new MessagePackModuleConfiguration.Configurator(configuration);

    public static StatelessModuleProxy<MessagePackModuleConfiguration> messagePackModule() {
        return messagePackModule.get();
    }
}
