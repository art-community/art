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

package io.art.meta.transformer;

import io.art.core.collection.*;
import lombok.*;
import static io.art.core.caster.Caster.*;
import static io.art.core.factory.ArrayFactory.*;
import static java.util.function.Function.*;
import static lombok.AccessLevel.*;
import java.util.*;

@AllArgsConstructor(access = PRIVATE)
public class ListTransformer implements MetaTransformer<List<?>> {
    @Override
    public List<?> fromArray(List<?> value) {
        return value;
    }

    @Override
    public List<?> toArray(List<?> value) {
        return value;
    }

    @Override
    public List<?> fromLazyArray(ImmutableLazyArrayImplementation<?> value) {
        return value.toMutable();
    }

    @Override
    public ImmutableLazyArrayImplementation<?> toLazyArray(List<?> value) {
        return cast(immutableLazyArrayOf(value, identity()));
    }

    public static ListTransformer LIST_TRANSFORMER = new ListTransformer();
}
