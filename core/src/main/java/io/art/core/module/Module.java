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

package io.art.core.module;

import io.art.core.context.*;
import static io.art.core.caster.Caster.*;
import java.util.function.*;

public interface Module<Configuration extends ModuleConfiguration, Configurator extends ModuleConfigurator<Configuration, Configurator>> {
    String getId();

    default void load(ContextService contextService) {

    }

    default void unload(ContextService contextService) {

    }

    default void beforeReload(ContextService contextService) {

    }

    default void afterReload(ContextService contextService) {

    }

    default void launch(ContextService contextService) {

    }

    default void shutdown(ContextService contextService) {

    }

    Configurator getConfigurator();

    default void configure(UnaryOperator<Configurator> configurator) {
        configurator.apply(cast(getConfigurator()));
    }
}
