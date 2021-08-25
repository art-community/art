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

package io.art.meta.descriptor;

import io.art.core.annotation.*;
import io.art.core.stream.*;
import io.art.meta.model.*;
import io.netty.buffer.*;
import static io.art.core.context.Context.*;
import java.io.*;
import java.nio.*;
import java.nio.charset.*;

@Public
public interface Reader {
    default <T> T read(MetaType<T> type, byte[] bytes) {
        return read(type, new ByteArrayInputStream(bytes));
    }

    default <T> T read(MetaType<T> type, ByteBuffer nioBuffer) {
        return read(type, new NioByteBufferInputStream(nioBuffer));
    }

    default <T> T read(MetaType<T> type, ByteBuf nettyBuffer) {
        return read(type, new ByteBufInputStream(nettyBuffer));
    }

    default <T> T read(MetaType<T> type, String string) {
        return read(type, string, context().configuration().getCharset());
    }

    default <T> T read(MetaType<T> type, String string, Charset charset) {
        return read(type, string.getBytes(charset));
    }

    <T> T read(MetaType<T> type, InputStream input);
}
