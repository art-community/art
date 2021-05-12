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

package io.art.model.customizer;

import io.art.core.annotation.*;
import io.art.logging.configuration.*;
import lombok.*;

@UsedByGenerator
public class LoggingCustomizer {
    @Getter
    private final Custom configuration = new Custom();

    public LoggingCustomizer colored() {
        return colored(true);
    }

    public LoggingCustomizer colored(boolean colored) {
        configuration.colored = colored;
        return this;
    }

    public LoggingCustomizer asynchronous() {
        return asynchronous(true);
    }

    public LoggingCustomizer asynchronous(boolean asynchronous) {
        configuration.asynchronous = asynchronous;
        return this;
    }

    @Getter
    private static class Custom extends LoggingModuleConfiguration {
        private Boolean colored;
        private Boolean asynchronous;
    }
}
