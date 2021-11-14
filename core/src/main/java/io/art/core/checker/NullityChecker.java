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

package io.art.core.checker;

import io.art.core.collection.*;
import lombok.experimental.*;
import static java.util.Objects.*;
import java.util.*;
import java.util.function.*;

@UtilityClass
public class NullityChecker {
    public static <T> T orElse(T value, T orElse) {
        return isNull(value) ? orElse : value;
    }

    public static <T> T orThrow(T value, Supplier<? extends RuntimeException> exceptionFactory) {
        if (isNull(value)) {
            throw exceptionFactory.get();
        }
        return value;
    }

    public static <T> T orThrow(T value, RuntimeException exception) {
        if (isNull(value)) {
            throw exception;
        }
        return value;
    }

    public static <T> T orElse(T value, Supplier<T> orElse) {
        return isNull(value) ? orElse.get() : value;
    }

    public static <T> void apply(T value, Runnable action) {
        if (nonNull(value)) {
            action.run();
        }
    }

    public static <T> void apply(T value, Consumer<T> consumer) {
        if (nonNull(value)) {
            consumer.accept(value);
        }
    }

    public static <T, R> R let(T value, Function<T, R> action) {
        return nonNull(value) ? action.apply(value) : null;
    }

    public static <T> T run(T value, Consumer<T> action) {
        if (isNull(value)) return null;
        action.accept(value);
        return null;
    }

    public static <T> T orNull(T value, Predicate<T> condition) {
        return condition.test(value) ? value : null;
    }

    public static <T> T orNull(T value, Supplier<Boolean> condition) {
        return condition.get() ? value : null;
    }

    public static <T> T orNull(T value, Boolean condition) {
        return condition ? value : null;
    }

    public static <T> T orNull(Supplier<T> value, Boolean condition) {
        return condition ? value.get() : null;
    }

    public static <T, R> R orNull(T value, Predicate<T> condition, Function<T, R> action) {
        return condition.test(value) ? action.apply(value) : null;
    }

    public static <T, R> R let(T value, Function<T, R> action, Supplier<R> orElse) {
        return nonNull(value) ? action.apply(value) : orElse.get();
    }

    public static <T, R> R let(T value, Function<T, R> action, R orElse) {
        return nonNull(value) ? action.apply(value) : orElse;
    }

    public <T> void forEach(Collection<T> value, Consumer<T> consumer) {
        if (isNull(value)) return;
        for (T element : value) {
            consumer.accept(element);
        }
    }

    public <T> void forEach(ImmutableCollection<T> value, Consumer<T> consumer) {
        if (isNull(value)) return;
        for (T element : value) {
            consumer.accept(element);
        }
    }
}
