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

package io.art.logging.messaging;

import io.art.logging.model.*;
import io.art.logging.writer.*;
import lombok.*;

@AllArgsConstructor
public class LoggerProducer {
    private final LoggingQueue queue;
    private final LoggerWriter fallbackWriter;

    public void produce(LoggingMessage message) {
        if (!queue.offer(message)) {
            fallbackWriter.write(message);
        }
    }
}
