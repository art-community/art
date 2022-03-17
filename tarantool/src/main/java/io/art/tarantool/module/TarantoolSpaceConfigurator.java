package io.art.tarantool.module;

import io.art.meta.model.*;
import io.art.storage.*;
import io.art.storage.index.*;
import io.art.storage.sharder.*;
import lombok.*;
import static io.art.core.caster.Caster.*;
import java.util.function.*;

@RequiredArgsConstructor
public class TarantoolSpaceConfigurator<C> {
    final Class<? extends Storage> storageClass;
    final Class<C> spaceClass;
    Supplier<MetaField<? extends MetaClass<C>, ?>> field;
    Class<? extends Indexes<C>> indexes;

    public <M extends MetaClass<C>> TarantoolSpaceConfigurator<C> id(Supplier<MetaField<M, ?>> field) {
        this.field = cast(field);
        return this;
    }

    public <I extends Indexes<C>> TarantoolSpaceConfigurator<C> indexes(Class<I> indexes) {
        this.indexes = indexes;
        return this;
    }

    public <P1> TarantoolSpaceConfigurator<C> sharded(ShardFunctionOperator1<C, P1> operator) {
        return this;
    }

    public <P1, P2> TarantoolSpaceConfigurator<C> sharded(ShardFunctionOperator2<C, P1, P2> operator) {
        return this;
    }
}
