package io.art.tarantool.service;

import io.art.core.model.*;
import io.art.meta.model.*;
import io.art.storage.*;
import io.art.storage.SpaceStream.*;
import io.art.tarantool.constants.TarantoolModuleConstants.*;
import io.art.tarantool.descriptor.*;
import io.art.tarantool.storage.*;
import lombok.*;
import org.msgpack.value.Value;
import org.msgpack.value.*;
import reactor.core.publisher.*;
import static io.art.core.caster.Caster.*;
import static io.art.core.collector.ArrayCollector.*;
import static io.art.core.factory.ListFactory.*;
import static io.art.meta.Meta.*;
import static io.art.tarantool.constants.TarantoolModuleConstants.FilterOptions.*;
import static io.art.tarantool.constants.TarantoolModuleConstants.Functions.*;
import static io.art.tarantool.constants.TarantoolModuleConstants.SortOptions.*;
import static org.msgpack.value.ValueFactory.*;
import static reactor.core.publisher.Flux.*;
import java.util.*;


@AllArgsConstructor
public class TarantoolReactiveStream<ModelType, MetaModel extends MetaClass<ModelType>> extends ReactiveSpaceStream<TarantoolReactiveStream<ModelType, MetaModel>, ModelType, MetaModel> {
    private final TarantoolStorage storage;
    private final TarantoolModelReader reader;
    private final TarantoolModelWriter writer;
    private final MetaType<ModelType> spaceMeta;

    @Override
    public Flux<ModelType> collect() {
        List<Value> serialized = linkedList();
        for (Pair<StreamOperation, Object> operator : operators) {
            switch (operator.getFirst()) {
                case LIMIT:
                    serialized.add(newArray(newString(SelectOptions.LIMIT), newInteger(cast(operator.getSecond()))));
                    break;
                case OFFSET:
                    serialized.add(newArray(newString(SelectOptions.OFFSET), newInteger(cast(operator.getSecond()))));
                    break;
                case DISTINCT:
                    serialized.add(newArray(newString(SelectOptions.DISTINCT)));
                    break;
                case SORT:
                    Sorter<ModelType, MetaModel, ?> sorter = cast(operator.getSecond());
                    Sorter.SortComparator comparator = sorter.getComparator();
                    MetaField<MetaModel, ?> field = sorter.getField();
                    switch (comparator) {
                        case MORE:
                            serialized.add(newArray(newString(COMPARATOR_MORE), newInteger(field.index())));
                            break;
                        case LESS:
                            serialized.add(newArray(newString(COMPARATOR_LESS), newInteger(field.index())));
                            break;
                    }
                    break;
                case FILTER:
                    Filter<ModelType, MetaModel, ?> filter = cast(operator.getSecond());
                    Filter.FilterOperator filterOperator = filter.getOperator();
                    field = filter.getCurrent();
                    List<Object> values = filter.getValues();
                    ImmutableArrayValue filterValues = newArray(values
                            .stream()
                            .map(value -> writer.write(definition(value.getClass()), value))
                            .collect(listCollector()));
                    switch (filterOperator) {
                        case EQUALS:
                            serialized.add(newArray(newString(OPERATOR_EQUALS), newInteger(field.index()), filterValues));
                            break;
                        case NOT_EQUALS:
                            serialized.add(newArray(newString(OPERATOR_NOT_EQUALS), newInteger(field.index()), filterValues));
                            break;
                        case MORE:
                            serialized.add(newArray(newString(OPERATOR_MORE), newInteger(field.index()), filterValues));
                            break;
                        case LESS:
                            serialized.add(newArray(newString(OPERATOR_LESS), newInteger(field.index()), filterValues));
                            break;
                        case IN:
                            serialized.add(newArray(newString(OPERATOR_IN), newInteger(field.index()), filterValues));
                            break;
                        case NOT_IN:
                            serialized.add(newArray(newString(OPERATOR_NOT_IN), newInteger(field.index()), filterValues));
                            break;
                        case LIKE:
                            serialized.add(newArray(newString(OPERATOR_LIKE), newInteger(field.index()), filterValues));
                            break;
                        case STARTS_WITH:
                            serialized.add(newArray(newString(OPERATOR_STARTS_WITH), newInteger(field.index()), filterValues));
                            break;
                        case ENDS_WITH:
                            serialized.add(newArray(newString(OPERATOR_ENDS_WITH), newInteger(field.index()), filterValues));
                            break;
                        case CONTAINS:
                            serialized.add(newArray(newString(OPERATOR_CONTAINS), newInteger(field.index()), filterValues));
                            break;
                    }
                    break;
            }
        }

        return parseSpaceFlux(storage.immutable().call(SPACE_FIND, newArray(serialized)));
    }

    private Flux<ModelType> parseSpaceFlux(Mono<Value> value) {
        return value.flatMapMany(elements -> fromStream(elements.asArrayValue()
                .list()
                .stream()
                .map(element -> reader.read(spaceMeta, element))));
    }
}