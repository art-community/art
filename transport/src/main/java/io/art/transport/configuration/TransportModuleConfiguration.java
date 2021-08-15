package io.art.transport.configuration;

import io.art.core.module.*;
import io.art.core.source.*;
import lombok.*;
import static io.art.transport.constants.TransportModuleConstants.ConfigurationKeys.*;

@Getter
public class TransportModuleConfiguration implements ModuleConfiguration {
    private TransportPoolConfiguration commonPoolConfiguration = TransportPoolConfiguration.defaults();

    @RequiredArgsConstructor
    public static class Configurator implements ModuleConfigurator<TransportModuleConfiguration, Configurator> {
        private final TransportModuleConfiguration configuration;

        @Override
        public Configurator from(ConfigurationSource source) {
            if (source.has(TRANSPORT_COMMON_SECTION)) {
                this.configuration.commonPoolConfiguration = TransportPoolConfiguration.from(source, configuration.getCommonPoolConfiguration());
            }
            return this;
        }

        @Override
        public Configurator initialize(TransportModuleConfiguration configuration) {
            this.configuration.commonPoolConfiguration = configuration.getCommonPoolConfiguration();
            return this;
        }
    }
}
