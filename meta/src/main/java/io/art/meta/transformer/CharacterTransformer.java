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
import static java.lang.Character.*;
import static lombok.AccessLevel.*;

@NoArgsConstructor(access = PRIVATE)
public class CharacterTransformer implements MetaTransformer<Character> {
    @Override
    public String toString(Character value) {
        return value.toString();
    }

    @Override
    public Character fromString(String value) {
        if (value.isEmpty()) return MIN_VALUE;
        return value.charAt(0);
    }

    @Override
    public Character fromCharacter(Character value) {
        return value;
    }

    @Override
    public Character toCharacter(Character value) {
        return value;
    }

    public static CharacterTransformer CHARACTER_TRANSFORMER = new CharacterTransformer();
}