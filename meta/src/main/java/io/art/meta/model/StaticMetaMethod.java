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

package io.art.meta.model;

import io.art.core.annotation.*;
import io.art.core.exception.*;
import lombok.*;

@ForGenerator
@EqualsAndHashCode(callSuper = true)
public abstract class StaticMetaMethod<T> extends MetaMethod<T> {
    protected StaticMetaMethod(String name, MetaType<T> returnType) {
        super(name, returnType);
    }

    public StaticMetaMethod(MetaMethod<T> base) {
        super(base);
    }

    public T invoke() {
        throw new NotImplementedException("invoke()");
    }

    public T invoke(Object argument) {
        throw new NotImplementedException("invoke(argument)");
    }

    public abstract T invoke(Object... arguments);

    @Override
    public boolean isStatic() {
        return true;
    }
}