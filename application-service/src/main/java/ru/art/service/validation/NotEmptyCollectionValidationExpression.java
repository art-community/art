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

package ru.art.service.validation;

import ru.art.service.constants.*;
import static ru.art.service.constants.ValidationExpressionType.*;
import java.text.*;
import java.util.*;

class NotEmptyCollectionValidationExpression extends ValidationExpression<Collection> {
    NotEmptyCollectionValidationExpression() {
        super(NOT_EMPTY_COLLECTION);
    }

    @Override
    public boolean evaluate(String fieldName, Collection value) {
        return super.evaluate(fieldName, value) && !value.isEmpty();
    }

    public String getValidationErrorMessage() {
        return MessageFormat.format(ServiceExceptionsMessages.EMPTY_VALIDATION_ERROR, fieldName);
    }
}
