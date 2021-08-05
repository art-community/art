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

import lombok.*;
import static io.art.core.extensions.DateTimeExtensions.*;
import static java.time.LocalDateTime.*;
import static java.util.Comparator.*;
import static java.util.concurrent.TimeUnit.*;
import static lombok.AccessLevel.*;
import javax.annotation.*;
import java.time.*;
import java.util.concurrent.*;
import java.util.function.*;

class DeferredEvent<EventResultType> implements Delayed {
    @Getter
    private final Future<EventResultType> task;

    @Getter(value = PACKAGE)
    private final long trigger;

    @Getter(value = PACKAGE)
    private final int order;

    DeferredEvent(Future<EventResultType> task, LocalDateTime triggerDateTime, int order) {
        this.task = task;
        this.trigger = toMillis(triggerDateTime);
        this.order = order;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(trigger - toMillis(now()), MILLISECONDS);
    }

    @Override
    public int compareTo(@Nonnull Delayed other) {
        return comparingLong((ToLongFunction<DeferredEvent<?>>) DeferredEvent::getTrigger)
                .thenComparingInt(DeferredEvent::getOrder)
                .compare(this, (DeferredEvent<?>) other);
    }
}
