package io.art.configurator.model;

import lombok.*;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class CustomConfiguration {
    private final String section;
    private final Class<?> type;
}
