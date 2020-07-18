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

package io.art.entity.xml;

import com.google.common.collect.*;
import io.art.entity.builder.*;
import io.art.entity.immutable.Value;
import io.art.entity.immutable.*;
import lombok.*;
import static io.art.core.caster.Caster.*;
import static io.art.core.checker.EmptinessChecker.*;
import static io.art.core.extensions.CollectionExtensions.*;
import static io.art.core.factory.CollectionsFactory.*;
import static io.art.entity.factory.ArrayFactory.*;
import static io.art.entity.factory.EntityFactory.*;
import static io.art.entity.factory.PrimitivesFactory.*;
import static io.art.entity.immutable.Entity.*;
import static java.util.Collections.*;
import static java.util.Objects.*;
import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.*;
import java.util.*;

@NoArgsConstructor(access = PRIVATE)
public final class XmlEntityToEntityConverter {


    /*

<entity>
    <int>123</int>
    <bool>false</bool>
    <float>123.0</float>
    <double>123.0</double>
    <long>123</long>
    <string>test</string>
    <object>
        <string>test</string>
        <string-2>test</string-2>
    </object>
    <array>
        <test></test>
        <test></test>
    </array>
    <objects>
        <string>test</string>
        <string>test</string>
    </objects>
    <innerArray>
        <string>test</string>
        <innerArray>
            <string>test</string>
        </innerArray>
        <string>test</string>
        <innerArray>
            <string>test</string>
        </innerArray>
    </innerArray>
</entity>

     */
    public static Entity toEntityFromTags(XmlEntity xmlEntity) {
        if (Value.valueIsNull(xmlEntity)) return null;
        EntityBuilder entityBuilder = entityBuilder();
        String value = xmlEntity.getValue();
        if (isNotEmpty(value)) {
            entityBuilder.put(xmlEntity.getTag(), stringPrimitive(value));
        }
        ImmutableList<XmlEntity> children = xmlEntity.getChildren();
        if (isEmpty(children)) return entityBuilder.build();
        if (areAllUnique(children.stream().map(XmlEntity::getTag).collect(toList()))) {
            EntityBuilder innerEntityBuilder = entityBuilder();
            for (XmlEntity child : children) {
                if (isEmpty(child.getChildren())) {
                    innerEntityBuilder.put(child.getTag(), stringPrimitive(child.getValue()));
                    continue;
                }
                Entity innerEntity = toEntityFromTags(child);
                if (isNull(innerEntity)) continue;
                innerEntityBuilder.put(child.getTag(), innerEntity.get(child.getTag()));
            }
            return entityBuilder.put(xmlEntity.getTag(), innerEntityBuilder.build()).build();
        }
        ImmutableList.Builder<Value> collection = ImmutableList.builderWithExpectedSize(children.size());
        for (XmlEntity child : children) {
            if (isEmpty(child.getChildren()) && isEmpty(child.getValue())) {
                collection.add(stringPrimitive(child.getTag()));
                continue;
            }
            if (isEmpty(child.getChildren())) {
                collection.add(entityBuilder().put(child.getTag(), stringPrimitive(child.getValue())).build());
                continue;
            }
            Entity entity = toEntityFromTags(child);
            if (nonNull(entity)) {
                collection.add(entity);
            }
        }
        return entityBuilder.put(xmlEntity.getTag(), array(collection.build())).build();
    }

    public static Entity toEntityFromAttributes(XmlEntity xmlEntity) {
        if (Value.valueIsEmpty(xmlEntity)) {
            return null;
        }
        Map<Primitive, Primitive> attributes = xmlEntity.getAttributes()
                .entrySet()
                .stream()
                .collect(toMap(entry -> stringPrimitive(entry.getKey()), entry -> stringPrimitive(entry.getValue())));
        return entityBuilder().put(xmlEntity.getTag(), entity(attributes.keySet(), attributes::get)).build();
    }
}
