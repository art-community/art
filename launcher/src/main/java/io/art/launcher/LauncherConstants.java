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

package io.art.launcher;

import io.art.core.collection.*;
import static io.art.core.constants.ModuleIdentifiers.*;
import static io.art.core.factory.SetFactory.*;

public interface LauncherConstants {
    interface Errors {
        String MODULES_ALREADY_LAUNCHED = "Modules already launched";
    }

    ImmutableSet<String> LAUNCHED_MESSAGES = immutableSetOf(
            "Initialization completed",
            "Have a nice work with ART!"
    );

    String CONFIGURED_BY_MESSAGE = "Configured by {0}";
    String DEFAULT_CONFIGURATION = "Configurator not activated. Using default configurations";

    String LAUNCHER_LOGGER = "launcher";

    ImmutableSet<String> MODULE_PRE_LOADING_ORDER = ImmutableSet.<String>immutableSetBuilder()
            .add(CONFIGURATOR_MODULE_ID)
            .add(LOGGING_MODULE_ID)
            .build();

    ImmutableSet<String> MODULE_POST_LOADING_ORDER = ImmutableSet.<String>immutableSetBuilder()
            .add(META_MODULE_ID)
            .build();
}
