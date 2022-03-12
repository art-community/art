package io.art.storage.index;

import io.art.meta.model.*;
import static java.util.stream.Collectors.*;
import java.util.*;

public interface Index {
    List<MetaField<?, ?>> fields();

    default MetaField<?, ?> field(int id) {
        return fields().get(id);
    }

    default MetaField<?, ?> first() {
        return field(0);
    }

    default String name() {
        return fields().stream().map(MetaField::name).collect(joining());
    }
}
