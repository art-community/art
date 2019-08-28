/*
 * ART Java
 *
 * Copyright 2019 ART
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

package ru.art.example.api.model;

import lombok.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;
import java.util.*;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class ExampleRequest implements Validatable {
    private ExampleModel innerModel;
    private Integer primitiveTypeInt;
    private List<String> collection;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("innerModel", innerModel, notNull());
        validator.validate("primitiveTypeInt", primitiveTypeInt, notNull());
        validator.validate("collection", collection, notNull());
    }
}
