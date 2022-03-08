package io.art.storage;

import io.art.meta.model.*;
import lombok.*;
import static io.art.core.caster.Caster.*;
import static io.art.storage.StorageConstants.FilterOperator.*;

@Getter
public class FilterBySpaceUseNumberFields<Current, Other> extends FilterBySpaceUseFields<Current, Other, Number> {
    FilterBySpaceUseNumberFields(FilterRule<Current> rule) {
        super(rule);
    }

    public FilterRule<Current> moreThan(MetaField<? extends MetaClass<Other>, ? extends Number> other) {
        this.operator = MORE;
        fields.add(cast(other));
        return rule;
    }

    public FilterRule<Current> lessThan(MetaField<? extends MetaClass<Other>, ? extends Number> other) {
        this.operator = LESS;
        fields.add(cast(other));
        return rule;
    }

    public FilterRule<Current> between(MetaField<? extends MetaClass<Other>, ? extends Number> otherStart, MetaField<? extends MetaClass<Other>, ? extends Number> otherEnd) {
        this.operator = BETWEEN;
        fields.add(cast(otherStart));
        fields.add(cast(otherEnd));
        return rule;
    }

    public FilterRule<Current> notBetween(MetaField<? extends MetaClass<Other>, ? extends Number> otherStart, MetaField<? extends MetaClass<Other>, ? extends Number> otherEnd) {
        this.operator = NOT_BETWEEN;
        fields.add(cast(otherStart));
        fields.add(cast(otherEnd));
        return rule;
    }
}
