package io.art.storage.service;

import io.art.core.annotation.*;
import io.art.core.collection.*;
import io.art.meta.model.*;
import io.art.storage.index.*;
import io.art.storage.stream.*;
import static io.art.core.caster.Caster.*;
import static io.art.core.constants.CompilerSuppressingWarnings.*;
import static java.util.Arrays.*;
import java.util.*;

@Public
@SuppressWarnings({UNCHECKED, VARARGS})
public interface SpaceService<KeyType, ModelType> {
    ModelType findFirst(KeyType key);

    default ImmutableArray<ModelType> findAll(KeyType... keys) {
        return findAll(asList(keys));
    }

    ImmutableArray<ModelType> findAll(Collection<KeyType> keys);

    ImmutableArray<ModelType> findAll(ImmutableCollection<KeyType> keys);

    ModelType delete(KeyType key);

    default ImmutableArray<ModelType> delete(KeyType... keys) {
        return delete(asList(keys));
    }

    ImmutableArray<ModelType> delete(Collection<KeyType> keys);

    ImmutableArray<ModelType> delete(ImmutableCollection<KeyType> keys);

    long count();

    void truncate();

    ModelType insert(ModelType value);

    default ImmutableArray<ModelType> insert(ModelType... value) {
        return insert(asList(value));
    }

    ImmutableArray<ModelType> insert(Collection<ModelType> value);

    ImmutableArray<ModelType> insert(ImmutableCollection<ModelType> value);

    ModelType put(ModelType value);

    default ImmutableArray<ModelType> put(ModelType... value) {
        return put(Arrays.asList(value));
    }

    ImmutableArray<ModelType> put(Collection<ModelType> value);

    ImmutableArray<ModelType> put(ImmutableCollection<ModelType> value);

    SpaceStream<ModelType> stream();

    ReactiveSpaceService<KeyType, ModelType> reactive();

    default <F1> Index1Service<ModelType, F1> index(Index1<ModelType, F1> index) {
        MetaField<MetaClass<ModelType>, ?> fields = cast(index.fields());
        return new Index1Service<>(index(fields));
    }

    IndexService<ModelType> index(MetaField<MetaClass<ModelType>, ?>... fields);
}
