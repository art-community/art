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

package io.art.core.extensions;

import lombok.experimental.*;
import static io.art.core.constants.BufferConstants.*;
import static io.art.core.extensions.InputStreamExtensions.*;
import static io.art.core.wrapper.ExceptionWrapper.*;
import java.io.*;

@UtilityClass
public class StreamsExtensions {
    public static void transferBytes(InputStream inputStream, OutputStream outputStream, int bufferSize) throws IOException {
        outputStream.write(toByteArray(inputStream, bufferSize));
    }

    public static void transferBytes(InputStream inputStream, OutputStream outputStream) throws IOException {
        transferBytes(inputStream, outputStream, DEFAULT_BUFFER_SIZE);
    }

    public static void closeQuietly(Closeable stream) {
        ignoreException(stream::close);
    }
}
