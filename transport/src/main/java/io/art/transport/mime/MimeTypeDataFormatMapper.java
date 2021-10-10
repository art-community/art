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

package io.art.transport.mime;

import io.art.core.mime.*;
import io.art.transport.constants.TransportModuleConstants.*;
import io.art.transport.exception.*;
import lombok.experimental.*;
import static io.art.core.constants.Errors.*;
import static io.art.core.constants.MimeTypeConstants.*;
import static io.art.core.mime.MimeTypes.*;
import static io.art.transport.constants.TransportModuleConstants.DataFormat.*;
import java.nio.charset.*;

@UtilityClass
public class MimeTypeDataFormatMapper {
    public static DataFormat fromMimeType(MimeType type) {
        if (APPLICATION_JSON.equals(type)) return JSON;
        if (APPLICATION_MESSAGE_PACK.equals(type)) return MESSAGE_PACK;
        if (APPLICATION_YAML.equals(type)) return YAML;
        if (APPLICATION_YML.equals(type)) return YAML;
        if (APPLICATION_OCTET_STREAM.equals(type)) return BYTES;
        throw new UnsupportedMimeTypeException(type);
    }

    public static DataFormat fromMimeType(MimeType type, DataFormat fallback) {
        if (APPLICATION_JSON.equals(type)) return JSON;
        if (APPLICATION_MESSAGE_PACK.equals(type)) return MESSAGE_PACK;
        if (APPLICATION_YAML.equals(type)) return YAML;
        if (APPLICATION_YML.equals(type)) return YAML;
        if (APPLICATION_OCTET_STREAM.equals(type)) return BYTES;
        return fallback;
    }

    public static MimeType toMimeType(DataFormat dataFormat) {
        switch (dataFormat) {
            case JSON:
                return APPLICATION_JSON;
            case MESSAGE_PACK:
                return APPLICATION_MESSAGE_PACK;
            case YAML:
                return APPLICATION_YAML;
            case BYTES:
                return APPLICATION_OCTET_STREAM;
        }
        throw new IllegalArgumentException(DATA_FORMAT_IS_NULL);
    }

    public static MimeType toMimeType(DataFormat dataFormat, Charset charset) {
        switch (dataFormat) {
            case JSON:
                return APPLICATION_JSON.withParameter(PARAM_CHARSET, charset.name());
            case MESSAGE_PACK:
                return APPLICATION_MESSAGE_PACK.withParameter(PARAM_CHARSET, charset.name());
            case YAML:
                return APPLICATION_YAML.withParameter(PARAM_CHARSET, charset.name());
            case BYTES:
                return APPLICATION_OCTET_STREAM.withParameter(PARAM_CHARSET, charset.name());
        }
        throw new IllegalArgumentException(DATA_FORMAT_IS_NULL);
    }
}
