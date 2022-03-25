package io.art.tarantool.registry;

import io.art.core.collection.*;
import io.art.core.property.*;
import io.art.storage.*;
import io.art.storage.index.*;
import io.art.storage.sharder.*;
import io.art.tarantool.service.schema.*;
import io.art.tarantool.service.space.*;
import lombok.*;
import static io.art.core.caster.Caster.*;
import static io.art.core.normalizer.ClassIdentifierNormalizer.*;

@Builder
public class TarantoolServiceRegistry {
    private final LazyProperty<ImmutableMap<String, TarantoolBlockingStorageService<?, ?>>> spaces;
    private final LazyProperty<ImmutableMap<String, TarantoolSchemaService>> schemas;
    private final LazyProperty<ImmutableMap<String, Indexes<?>>> indexes;
    private final LazyProperty<ImmutableMap<String, Sharders<?>>> sharders;

    public <KeyType, ValueType> TarantoolBlockingStorageService<KeyType, ValueType> getSpace(Class<ValueType> type) {
        return cast(spaces.get().get(idByDash(type)));
    }

    public TarantoolSchemaService getSchema(Class<? extends Storage> storageType) {
        System.out.println("schemas: " + schemas.hashCode());
        return cast(schemas.get().get(idByDash(storageType)));
    }

    public <V, T extends Indexes<V>> T getIndexes(Class<V> spaceType) {
        return cast(indexes.get().get(idByDash(spaceType)));
    }

    public <V, T extends Sharders<V>> T getSharders(Class<V> spaceType) {
        return cast(sharders.get().get(idByDash(spaceType)));
    }
}
