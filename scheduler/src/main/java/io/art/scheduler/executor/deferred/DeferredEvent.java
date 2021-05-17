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

package io.art.scheduler.executor.deferred;

import io.art.logging.module.*;
import lombok.*;
import static io.art.core.constants.CompilerSuppressingWarnings.*;
import static java.lang.System.*;
import static java.time.ZoneId.*;
import static java.util.Comparator.*;
import static lombok.AccessLevel.*;
import java.time.*;
import java.util.concurrent.*;
import java.util.function.*;

class DeferredEvent<EventResultType> implements Delayed {
    @Getter
    private final Future<EventResultType> task;

    @Getter(value = PACKAGE)
    private final long triggerDateTime;

    @Getter(value = PACKAGE)
    private final int order;

    DeferredEvent(Future<EventResultType> task, LocalDateTime triggerDateTime, int order) {
        this.task = task;
        this.triggerDateTime = triggerDateTime.atZone(systemDefault()).toInstant().toEpochMilli();
        this.order = order;
        LoggingModule.logger(DeferredEvent.class).info("Event: triggerDateTime = " + triggerDateTime + " order = " + order);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.toNanos(triggerDateTime - currentTimeMillis());
    }

    @Override
    public int compareTo(@SuppressWarnings(NULLABLE_PROBLEMS) Delayed other) {
        return comparingLong((ToLongFunction<DeferredEvent<?>>) DeferredEvent::getTriggerDateTime)
                .thenComparingInt(DeferredEvent::getOrder)
                .compare(this, (DeferredEvent<?>) other);
    }
}
