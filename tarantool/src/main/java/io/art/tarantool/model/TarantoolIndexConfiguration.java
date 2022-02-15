package io.art.tarantool.model;

import io.art.core.annotation.*;
import io.art.meta.model.*;
import io.art.tarantool.constants.TarantoolModuleConstants.*;
import lombok.*;
import lombok.experimental.*;
import static io.art.core.collector.ArrayCollector.*;
import static io.art.core.normalizer.ClassIdentifierNormalizer.*;
import static io.art.tarantool.model.TarantoolIndexPartConfiguration.*;
import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import java.util.*;

@Public
@Builder(toBuilder = true)
@Getter
@Accessors(fluent = true)
public class TarantoolIndexConfiguration {
    private final String spaceName;
    private final String indexName;
    private final Boolean ifNotExists;
    private final Integer id;
    private final IndexType type;
    private final Boolean unique;
    @Singular("part")
    private final List<TarantoolIndexPartConfiguration> parts;
    private final TarantoolRtreeIndexConfiguration rtreeConfiguration;
    private final TarantoolTreeIndexConfiguration treeConfiguration;
    private final TarantoolVinylIndexConfiguration vinylConfiguration;
    private final String sequence;
    private final String func;

    @Public
    @Builder
    @Getter
    @Accessors(fluent = true)
    public static class TarantoolRtreeIndexConfiguration {
        private final Integer dimension;
        private final String distance;
    }

    @Public
    @Builder
    @Getter
    @Accessors(fluent = true)
    public static class TarantoolTreeIndexConfiguration {
        private final Boolean hint;
    }

    @Public
    @Builder
    @Getter
    @Accessors(fluent = true)
    public static class TarantoolVinylIndexConfiguration {
        private final int bloomFrp;
        private final int pageSize;
        private final int rangeSize;
        private final int runCountPerLevel;
        private final int runSizeRatio;
    }

    @SafeVarargs
    public static <C, M extends MetaClass<C>> TarantoolIndexConfigurationBuilder indexFor(Class<C> type, MetaField<M, ?>... fields) {
        return builder()
                .spaceName(idByDash(type))
                .indexName(stream(fields).map(MetaField::name).collect(joining()))
                .parts(stream(fields).map(field -> indexPartFor(field).build()).collect(listCollector()));
    }
}
