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

package io.art.core.extensions;

import io.art.core.exception.*;
import lombok.experimental.*;
import static io.art.core.handler.ExceptionHandler.*;
import static java.lang.Thread.*;
import java.util.function.*;

@UtilityClass
public class ThreadExtensions {
    public static void thread(String name, Runnable runnable) {
        new Thread(runnable, name).start();
    }

    public static void thread(Runnable runnable) {
        new Thread(runnable).start();
    }

    public static void block() {
        consumeException((Function<Throwable, RuntimeException>) InternalRuntimeException::new)
                .run(() -> currentThread().join());
    }
}