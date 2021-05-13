/*
 * ART
 *
 * Copyright 2019-2021 ART
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.art.logging.module;

import io.art.core.context.*;
import io.art.core.module.*;
import io.art.logging.configuration.*;
import io.art.logging.logger.*;
import io.art.logging.manager.*;
import io.art.logging.reactor.*;
import io.art.logging.state.*;
import io.art.scheduler.manager.*;
import lombok.*;
import static io.art.core.context.Context.*;
import static io.art.core.extensions.ThreadExtensions.*;
import static java.lang.Runtime.*;
import static java.util.logging.LogManager.*;
import static lombok.AccessLevel.*;
import static reactor.util.Loggers.*;
import java.time.*;

@Getter
public class LoggingModule implements StatefulModule<LoggingModuleConfiguration, LoggingModuleConfiguration.Configurator, LoggingModuleState> {
    @Getter(lazy = true, value = PRIVATE)
    private static final StatefulModuleProxy<LoggingModuleConfiguration, LoggingModuleState> loggingModule = context().getStatefulModule(LoggingModule.class.getSimpleName());
    private final String id = LoggingModule.class.getSimpleName();
    private final LoggingModuleConfiguration configuration = new LoggingModuleConfiguration();
    private final LoggingModuleConfiguration.Configurator configurator = new LoggingModuleConfiguration.Configurator(configuration);
    private final LoggingModuleState state = new LoggingModuleState();
    private final LoggingManager manager = new LoggingManager(configuration, state);
    private static LoggingManager DEFAULT_MANAGER;
    private static Thread DEACTIVATE_MANAGER;

    static {
        registerDefault(LoggingModule.class.getSimpleName(), LoggingModule::new);
    }

    @Override
    public void onDefaultLoad(Context.Service contextService) {
        DEFAULT_MANAGER = new LoggingManager(configuration, state).activate();
        getRuntime().addShutdownHook(DEACTIVATE_MANAGER = new Thread(DEFAULT_MANAGER::deactivate));
    }

    @Override
    public void onDefaultUnload(Context.Service contextService) {
        DEFAULT_MANAGER.deactivate();
        getRuntime().removeShutdownHook(DEACTIVATE_MANAGER);
    }

    @Override
    public void onLoad(Context.Service contextService) {
        getLogManager().reset();
        useCustomLoggers(name -> new ReactorLogger(logger(name)));
        manager.activate();
    }

    @Override
    public void afterReload(Context.Service contextService) {
        getLogManager().reset();
    }

    @Override
    public void onUnload(Context.Service contextService) {
        manager.deactivate();
        state.close();
    }

    public static StatefulModuleProxy<LoggingModuleConfiguration, LoggingModuleState> loggingModule() {
        return getLoggingModule();
    }

    public static Logger logger() {
        return logger(LoggingModule.class);
    }

    public static Logger logger(Class<?> nameByClass) {
        return logger(nameByClass.getName());
    }

    public static Logger logger(String name) {
        LoggingModuleConfiguration configuration = loggingModule().configuration();
        LoggingModuleState state = loggingModule().state();
        LoggerConfiguration loggerConfiguration = configuration.getLoggers().getOrDefault(name, configuration.getDefaultLogger().toLoggerConfiguration());
        return state.cachedLogger(name, () -> new LoggerImplementation(name, loggerConfiguration, state));
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            logger(LoggingModule.class).info("test:  " + i);
        }
        SchedulersManager.scheduleDelayed(() -> logger(LoggingModule.class).info("test:  "), Duration.ofSeconds(1));
        block();
    }
}