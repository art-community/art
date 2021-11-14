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

import io.art.core.extensions.*;
import io.netty.buffer.*;
import lombok.*;
import static lombok.AccessLevel.*;

@NoArgsConstructor(access = PRIVATE)
public class NettyBufferTransformer implements MetaTransformer<ByteBuf> {

    @Override
    public byte[] toByteArray(ByteBuf value) {
        return NettyBufferExtensions.toByteArray(value);
    }

    @Override
    public ByteBuf fromByteArray(byte[] value) {
        return NettyBufferExtensions.from(value);
    }

    public static NettyBufferTransformer NETTY_BUFFER_TRANSFORMER = new NettyBufferTransformer();
}
