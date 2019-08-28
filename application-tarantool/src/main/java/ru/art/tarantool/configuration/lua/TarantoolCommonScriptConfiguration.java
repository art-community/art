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

package ru.art.tarantool.configuration.lua;

import lombok.*;
import org.jtwig.*;
import static org.jtwig.JtwigTemplate.*;
import static ru.art.tarantool.constants.TarantoolModuleConstants.*;
import static ru.art.tarantool.constants.TarantoolModuleConstants.TemplateParameterKeys.*;
import static ru.art.tarantool.constants.TarantoolModuleConstants.Templates.*;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(staticName = "tarantoolCommonScript")
public class TarantoolCommonScriptConfiguration {
    private final String spaceName;

    public String toLua() {
        return classpathTemplate(COMMON + JTW_EXTENSION)
                .render(new JtwigModel().with(SPACE_NAME, spaceName));
    }

}
