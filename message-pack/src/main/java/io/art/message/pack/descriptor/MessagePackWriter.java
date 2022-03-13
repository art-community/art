/*
 * ART
 *
 * Copyright 2019-2022 ART
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

package io.art.message.pack.descriptor;

import io.art.core.annotation.*;
import io.art.core.collection.*;
import io.art.core.exception.*;
import io.art.message.pack.exception.MessagePackException;
import io.art.meta.descriptor.Writer;
import io.art.meta.model.*;
import io.art.meta.schema.MetaProviderTemplate.*;
import io.art.meta.transformer.*;
import io.netty.buffer.*;
import org.msgpack.core.*;
import org.msgpack.value.*;
import static io.art.core.caster.Caster.*;
import static io.art.core.checker.NullityChecker.*;
import static io.art.core.constants.StringConstants.*;
import static java.nio.channels.Channels.*;
import static java.util.Objects.*;
import static org.msgpack.core.MessagePack.*;
import static org.msgpack.value.ValueFactory.*;
import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;

@Public
public class MessagePackWriter implements Writer {
    @Override
    public void write(TypedObject model, ByteBuffer buffer) {
        write(model, buffer, MessagePackException::new);
    }

    @Override
    public void write(TypedObject model, ByteBuf buffer) {
        write(model, buffer, MessagePackException::new);
    }

    @Override
    public void write(TypedObject object, OutputStream outputStream, Charset charset) {
        if (isNull(object)) return;
        try (MessagePacker packer = newDefaultPacker(newChannel(outputStream))) {
            packer.packValue(write(object.getType(), object.getObject()));
        } catch (Throwable throwable) {
            throw new MessagePackException(throwable);
        }
    }

    public org.msgpack.value.Value write(MetaType<?> type, Object value) {
        if (isNull(value)) return newNil();
        MetaTransformer<?> transformer = type.outputTransformer();
        switch (type.externalKind()) {
            case MAP:
            case LAZY_MAP:
                return writeMap(type.parameters().get(0), type.parameters().get(1), transformer.toMap(cast(value)));
            case ARRAY:
            case LAZY_ARRAY:
                return writeArray(orElse(type.arrayComponentType(), () -> type.parameters().get(0)), transformer.toArray(cast(value)));
            case LAZY:
                return write(type.parameters().get(0), transformer.toLazy(cast(value)).get());
            case STRING:
                return newString(transformer.toString(cast(value)));
            case LONG:
                return newInteger(transformer.toLong(cast(value)));
            case DOUBLE:
                return newFloat(transformer.toDouble(cast(value)));
            case FLOAT:
                return newFloat(transformer.toFloat(cast(value)));
            case INTEGER:
                return newInteger(transformer.toInteger(cast(value)));
            case BOOLEAN:
                return newBoolean(transformer.toBoolean(cast(value)));
            case CHARACTER:
                return newString(EMPTY_STRING + transformer.toCharacter(cast(value)));
            case SHORT:
                return newInteger(transformer.toShort(cast(value)));
            case BYTE:
                return newInteger(transformer.toByte(cast(value)));
            case BINARY:
                return newBinary(transformer.toByteArray(cast(value)));
            case ENTITY:
                return writeEntity(type, cast(value));
        }
        throw new ImpossibleSituationException();
    }


    private org.msgpack.value.ArrayValue writeArray(MetaType<?> elementType, List<?> array) {
        org.msgpack.value.Value[] values = new Value[array.size()];
        int index = 0;
        for (Object element : array) values[index++] = write(elementType, element);
        return newArray(values, true);
    }

    private org.msgpack.value.MapValue writeEntity(MetaType<?> type, Object value) {
        MapBuilder mapBuilder = newMapBuilder();
        MetaProviderInstance provider = type.declaration().provider().instantiate(value);
        ImmutableMap<String, MetaProperty<?>> properties = provider.propertyMap();
        for (MetaProperty<?> property : properties.values()) {
            mapBuilder.put(newString(property.name()), write(property.type(), provider.getValue(property)));
        }
        return mapBuilder.build();
    }

    private org.msgpack.value.MapValue writeMap(MetaType<?> keyType, MetaType<?> valueType, Map<?, ?> value) {
        MapBuilder mapBuilder = newMapBuilder();
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            Object entryKey = entry.getKey();
            Object entryValue = entry.getValue();
            if (isNull(entryKey)) continue;
            mapBuilder.put(write(keyType, entryKey), write(valueType, entryValue));
        }
        return mapBuilder.build();
    }
}
