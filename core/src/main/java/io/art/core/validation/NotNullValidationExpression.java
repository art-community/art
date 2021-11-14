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

package io.art.core.validation;

import static io.art.core.constants.ValidationConstants.ValidationErrorPatterns.*;
import static io.art.core.constants.ValidationConstants.ValidationExpressionTypes.*;
import static java.text.MessageFormat.*;
import java.util.function.*;

public class NotNullValidationExpression extends ValidationExpression<Object> {
    NotNullValidationExpression() {
        super(NOT_NULL);
    }

    NotNullValidationExpression(Function<NotNullValidationExpression, String> factory) {
        super(NOT_NULL);
        this.messageFactory = factory;
    }

    @Override
    public boolean evaluate(String field, Object value) {
        return super.evaluate(field, value);
    }

    @Override
    public String formatErrorMessage() {
        return format(NULL_VALIDATION_ERROR, field);
    }
}
