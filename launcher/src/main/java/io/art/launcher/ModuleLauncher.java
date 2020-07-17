/*
 * ART
 *
 * Copyright 2020 ART
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

package io.art.launcher;

import io.art.core.factory.*;
import io.art.entity.immutable.*;
import io.art.entity.tuple.*;
import io.art.entity.xml.*;
import io.art.json.descriptor.*;
import io.art.json.module.*;
import io.art.message.pack.descriptor.*;
import io.art.protobuf.descriptor.*;
import io.art.xml.descriptor.*;
import io.art.xml.module.*;
import static io.art.core.context.Context.*;
import static io.art.core.factory.CollectionsFactory.fixedArrayOf;
import static io.art.entity.factory.ArrayFactory.array;
import static io.art.entity.factory.PrimitivesFactory.*;
import static io.art.entity.immutable.Entity.*;
import static io.art.entity.xml.XmlEntityFromEntityConverter.*;
import static io.art.entity.xml.XmlEntityToEntityConverter.*;
import static io.art.json.descriptor.JsonEntityReader.*;
import static io.art.json.descriptor.JsonEntityWriter.*;
import static io.art.message.pack.descriptor.MessagePackEntityReader.readMessagePack;
import static io.art.message.pack.descriptor.MessagePackEntityWriter.*;
import static io.art.protobuf.descriptor.ProtobufEntityReader.*;
import static io.art.protobuf.descriptor.ProtobufEntityWriter.*;
import static io.art.xml.descriptor.XmlEntityReader.*;
import static io.art.xml.descriptor.XmlEntityWriter.*;
import java.util.concurrent.atomic.*;

public class ModuleLauncher {
    private final static AtomicBoolean launched = new AtomicBoolean(false);

    public static void main(String[] args) {
        context().loadModule(new JsonModule()).loadModule(new XmlModule());
        Entity entity = entityBuilder()
                .lazyPut("int", () -> intPrimitive(123))
                .lazyPut("bool", () -> boolPrimitive(false))
                .lazyPut("float", () -> floatPrimitive(123))
                .lazyPut("double", () -> doublePrimitive(123))
                .lazyPut("long", () -> longPrimitive(123L))
                .lazyPut("string", () -> stringPrimitive("test"))
                .lazyPut("embedded", () -> entityBuilder()
                        .lazyPut("string", () -> stringPrimitive("test"))
                        .build()
                )
                .lazyPut("array", () -> array(fixedArrayOf(stringPrimitive("test"))))
                .build();
        System.out.println(writeJson(readJson(writeJson(entity))));
        System.out.println(writeProtobuf(readProtobuf(writeProtobuf(entity))));
        System.out.println(writeMessagePack(readMessagePack(writeMessagePack(entity))));
        System.out.println(PlainTupleWriter.writeTuple(PlainTupleReader.readTuple(PlainTupleWriter.writeTuple(entity).getTuple(),PlainTupleWriter.writeTuple(entity).getSchema())));
        System.out.println(writeXml(fromEntityAsTags(toEntityFromTags(readXml(writeXml(fromEntityAsTags(entity)))))));
        System.out.println(writeXml(fromEntityAsAttributes("root", toEntityFromAttributes(readXml(writeXml(fromEntityAsAttributes("root", entity)))))));
    }
}
