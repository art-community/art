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

package io.art.core.constants;

import io.art.core.collection.*;
import static io.art.core.factory.SetFactory.*;

public interface LoggingMessages {
    ImmutableSet<String> LAUNCHED_MESSAGES = immutableSetOf(
            "Welcome to ART!",
            "Initialization completed. All modules ready"
    );
    String SHUTDOWN_MESSAGE = "By, by! ART hopes see you soon";
    String MODULE_RELOADING_START_MESSAGE = "Module: ''{0}'' reloading...";
    String MODULE_RELOADING_END_MESSAGE = "Module: ''{0}'' reloaded";
}
