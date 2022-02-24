package io.art.tarantool.service;

import io.art.core.annotation.*;
import io.art.core.collection.*;
import io.art.meta.model.*;
import io.art.storage.*;
import io.art.tarantool.descriptor.*;
import io.art.tarantool.storage.*;
import lombok.*;
import org.msgpack.value.Value;
import org.msgpack.value.*;
import reactor.core.publisher.*;
import static io.art.core.caster.Caster.*;
import static io.art.core.collector.ArrayCollector.*;
import static io.art.core.normalizer.ClassIdentifierNormalizer.*;
import static io.art.meta.Meta.*;
import static io.art.tarantool.constants.TarantoolModuleConstants.Functions.*;
import static io.art.tarantool.module.TarantoolModule.*;
import static org.msgpack.value.ValueFactory.*;
import static reactor.core.publisher.Flux.*;
import java.util.*;

@Public
@RequiredArgsConstructor
public class TarantoolReactiveSpaceService<KeyType, ModelType> implements ReactiveSpaceService<KeyType, ModelType> {
    final Class<ModelType> spaceType;
    final StringValue spaceName;
    final MetaType<ModelType> spaceMetaType;
    final MetaClass<ModelType> spaceMetaClass;
    final MetaType<KeyType> keyMeta;
    final TarantoolStorage storage;
    final TarantoolModelWriter writer;
    final TarantoolModelReader reader;
    private final static MetaType<Long> LONG_TYPE = definition(Long.class);
    private TarantoolReactiveStream<ModelType> stream;

    public TarantoolReactiveSpaceService(MetaType<KeyType> keyMeta, MetaClass<ModelType> spaceMeta, TarantoolStorage storage) {
        this.spaceType = spaceMeta.definition().type();
        this.storage = storage;
        this.spaceMetaType = spaceMeta.definition();
        this.spaceMetaClass = spaceMeta;
        this.keyMeta = keyMeta;
        this.spaceName = newString(idByDash(spaceType));
        stream = new TarantoolReactiveStream<>(this);
        writer = tarantoolModule().configuration().getWriter();
        reader = tarantoolModule().configuration().getReader();
    }

    @Override
    public Mono<ModelType> findFirst(KeyType key) {
        ArrayValue input = newArray(spaceName, writer.write(keyMeta, key));
        Mono<Value> output = storage.immutable().call(SPACE_FIND_FIRST, input);
        return parseSpaceMono(output);
    }


    @Override
    public Flux<ModelType> findAll(Collection<KeyType> keys) {
        ArrayValue input = newArray(spaceName, newArray(keys.stream().map(key -> writer.write(keyMeta, key)).collect(listCollector())));
        Mono<Value> output = storage.immutable().call(SPACE_FIND_ALL, input);
        return parseSpaceFlux(output);
    }

    @Override
    public Flux<ModelType> findAll(ImmutableCollection<KeyType> keys) {
        ArrayValue input = newArray(spaceName, newArray(keys.stream().map(key -> writer.write(keyMeta, key)).collect(listCollector())));
        Mono<Value> output = storage.immutable().call(SPACE_FIND_ALL, input);
        return parseSpaceFlux(output);
    }


    @Override
    public Mono<ModelType> delete(KeyType key) {
        ArrayValue input = newArray(spaceName, writer.write(keyMeta, key));
        Mono<Value> output = storage.immutable().call(SPACE_SINGLE_DELETE, input);
        return parseSpaceMono(output);
    }

    @Override
    public Flux<ModelType> delete(Collection<KeyType> keys) {
        ArrayValue input = newArray(spaceName, newArray(keys.stream().map(key -> writer.write(keyMeta, key)).collect(listCollector())));
        Mono<Value> output = storage.immutable().call(SPACE_MULTIPLE_DELETE, input);
        return parseSpaceFlux(output);
    }

    @Override
    public Flux<ModelType> delete(ImmutableCollection<KeyType> keys) {
        ArrayValue input = newArray(spaceName, newArray(keys.stream().map(key -> writer.write(keyMeta, key)).collect(listCollector())));
        Mono<Value> output = storage.immutable().call(SPACE_MULTIPLE_DELETE, input);
        return parseSpaceFlux(output);
    }


    @Override
    public Mono<ModelType> insert(ModelType value) {
        ArrayValue input = newArray(spaceName, writer.write(spaceMetaType, value));
        Mono<Value> output = storage.immutable().call(SPACE_SINGLE_INSERT, input);
        return parseSpaceMono(output);
    }

    @Override
    public Flux<ModelType> insert(Collection<ModelType> value) {
        ArrayValue input = newArray(spaceName, newArray(value.stream().map(element -> writer.write(spaceMetaType, element)).collect(listCollector())));
        Mono<Value> output = storage.immutable().call(SPACE_MULTIPLE_INSERT, input);
        return parseSpaceFlux(output);
    }

    @Override
    public Flux<ModelType> insert(ImmutableCollection<ModelType> value) {
        ArrayValue input = newArray(spaceName, newArray(value.stream().map(element -> writer.write(spaceMetaType, element)).collect(listCollector())));
        Mono<Value> output = storage.immutable().call(SPACE_MULTIPLE_INSERT, input);
        return parseSpaceFlux(output);
    }


    @Override
    public Mono<ModelType> put(ModelType value) {
        ArrayValue input = newArray(spaceName, writer.write(spaceMetaType, value));
        Mono<Value> output = storage.immutable().call(SPACE_SINGLE_PUT, input);
        return parseSpaceMono(output);
    }

    @Override
    public Flux<ModelType> put(Collection<ModelType> value) {
        ArrayValue input = newArray(spaceName, newArray(value.stream().map(element -> writer.write(spaceMetaType, element)).collect(listCollector())));
        Mono<Value> output = storage.immutable().call(SPACE_MULTIPLE_PUT, input);
        return parseSpaceFlux(output);
    }

    @Override
    public Flux<ModelType> put(ImmutableCollection<ModelType> value) {
        ArrayValue input = newArray(spaceName, newArray(value.stream().map(element -> writer.write(spaceMetaType, element)).collect(listCollector())));
        Mono<Value> output = storage.immutable().call(SPACE_MULTIPLE_PUT, input);
        return parseSpaceFlux(output);
    }

    @Override
    public Mono<Long> count() {
        Mono<Value> output = storage.mutable().call(SPACE_COUNT, newArray(spaceName));
        return parseCountMono(output);
    }

    @Override
    public Mono<Void> truncate() {
        return storage.mutable().call(SPACE_TRUNCATE, newArray(spaceName)).then();
    }

    @Override
    public TarantoolReactiveStream<ModelType> stream() {
        return cast(stream);
    }

    private Mono<Long> parseCountMono(Mono<Value> value) {
        return value.map(element -> reader.read(LONG_TYPE, element));
    }

    private Mono<ModelType> parseSpaceMono(Mono<Value> value) {
        return value.map(element -> reader.read(spaceMetaType, element));
    }

    Flux<ModelType> parseSpaceFlux(Mono<Value> value) {
        return value.flatMapMany(elements -> fromStream(elements.asArrayValue()
                .list()
                .stream()
                .map(element -> reader.read(spaceMetaType, element))));
    }
}
