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

package io.art.meta.transformer;

import lombok.*;
import static io.art.core.constants.DateTimeConstants.*;
import static io.art.core.extensions.DateTimeExtensions.*;
import static lombok.AccessLevel.*;
import java.time.*;

@NoArgsConstructor(access = PRIVATE)
public class ZonedDateTimeTransformer implements MetaTransformer<ZonedDateTime> {
    @Override
    public String toString(ZonedDateTime value) {
        return TRANSPORTABLE_FORMATTER.format(value);
    }

    @Override
    public ZonedDateTime fromString(String value) {
        return ZonedDateTime.parse(value, TRANSPORTABLE_FORMATTER);
    }

    @Override
    public ZonedDateTime fromLong(Long value) {
        return zonedFromMillis(value);
    }

    @Override
    public Long toLong(ZonedDateTime value) {
        return toMillis(value);
    }

    public static ZonedDateTimeTransformer ZONED_DATE_TIME_TRANSFORMER = new ZonedDateTimeTransformer();
}
