package io.art.storage.service;

import io.art.core.annotation.*;
import io.art.core.collection.*;
import io.art.core.model.*;
import io.art.storage.stream.*;
import io.art.storage.updater.*;
import lombok.*;
import lombok.experimental.*;
import static io.art.core.model.Tuple.*;
import java.util.*;
import java.util.function.*;

@Public
@Getter
@Accessors(fluent = true)
public class BlockingIndex2Service<SpaceType, F1, F2> {
    private final BlockingIndexService<SpaceType> delegate;
    private final ReactiveIndex2Service<SpaceType, F1, F2> reactive;

    public BlockingIndex2Service(BlockingIndexService<SpaceType> delegate) {
        this.delegate = delegate;
        reactive = new ReactiveIndex2Service<>(delegate.reactive());
    }

    public SpaceType first(F1 key1, F2 key2) {
        return delegate.first(tuple(key1, key2));
    }

    public SpaceType update(F1 key1, F2 key2, UnaryOperator<Updater<SpaceType>> updater) {
        return delegate.update(tuple(key1, key2), updater);
    }

    public ImmutableArray<SpaceType> update(Collection<Tuple2<F1, F2>> keys, UnaryOperator<Updater<SpaceType>> updater) {
        return delegate.update(keys, updater);
    }

    public ImmutableArray<SpaceType> update(ImmutableCollection<Tuple2<F1, F2>> keys, UnaryOperator<Updater<SpaceType>> updater) {
        return delegate.update(keys, updater);
    }

    public ImmutableArray<SpaceType> select(F1 key1, F2 key2) {
        return delegate.select(tuple(key1, key2));
    }

    public ImmutableArray<SpaceType> select(F1 key1, F2 key2, int offset, int limit) {
        return delegate.select(tuple(key1, key2), offset, limit);
    }

    @SafeVarargs
    public final ImmutableArray<SpaceType> find(Tuple2<F1, F2>... keys) {
        return delegate.find(keys);
    }

    public ImmutableArray<SpaceType> find(Collection<Tuple2<F1, F2>> keys) {
        return delegate.find(keys);
    }

    public ImmutableArray<SpaceType> find(ImmutableCollection<Tuple2<F1, F2>> keys) {
        return delegate.find(keys);
    }

    public final SpaceType delete(F1 key1, F2 key2) {
        return delegate.delete(tuple(key1, key2));
    }

    @SafeVarargs
    public final ImmutableArray<SpaceType> delete(Tuple2<F1, F2>... keys) {
        return delegate.delete(keys);
    }

    public ImmutableArray<SpaceType> delete(Collection<Tuple2<F1, F2>> keys) {
        return delegate.delete(keys);
    }

    public ImmutableArray<SpaceType> delete(ImmutableCollection<Tuple2<F1, F2>> keys) {
        return delegate.delete(keys);
    }

    public long count(F1 key1, F2 key2) {
        return delegate.count(tuple(key1, key2));
    }

    public BlockingSpaceStream<SpaceType> stream() {
        return delegate.stream();
    }

    public BlockingSpaceStream<SpaceType> stream(F1 key1, F2 key2) {
        return delegate.stream(tuple(key1, key2));
    }
}
