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

package io.art.meta.state;

import io.art.core.collection.*;
import io.art.meta.model.*;
import io.art.meta.validator.*;
import lombok.experimental.*;
import static io.art.core.collection.ImmutableArray.*;
import static io.art.core.factory.ArrayFactory.*;
import static io.art.core.factory.MapFactory.*;
import java.util.*;

@UtilityClass
public class MetaComputationState {
    private final static Map<MetaType<?>, ValidationResult> typeValidationResults = map();
    private final static Map<MetaClass<?>, ValidationResult> classValidationResults = map();

    public static void rememberValidation(MetaType<?> type, ValidationResult result) {
        typeValidationResults.put(type, result);
    }

    public static void rememberValidation(MetaClass<?> metaClass, ValidationResult result) {
        classValidationResults.put(metaClass, result);
    }

    public static ImmutableArray<ValidationResult> getValidationErrors() {
        List<ValidationResult> results = dynamicArrayOf(typeValidationResults.values());
        results.addAll(classValidationResults.values());
        return results.stream().filter(result -> !result.isValid()).collect(immutableArrayCollector());
    }
}
