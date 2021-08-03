package io.art.communicator.configurator;

import io.art.communicator.action.*;
import io.art.communicator.action.CommunicatorAction.*;
import io.art.communicator.configuration.*;
import io.art.communicator.model.*;
import io.art.core.collection.*;
import io.art.core.model.*;
import io.art.core.property.*;
import io.art.meta.model.*;
import lombok.*;
import static io.art.communicator.factory.CommunicatorProxyFactory.*;
import static io.art.core.caster.Caster.*;
import static io.art.core.checker.EmptinessChecker.*;
import static io.art.core.checker.NullityChecker.*;
import static io.art.core.collection.ImmutableMap.*;
import static io.art.core.factory.ArrayFactory.*;
import static io.art.core.factory.ListFactory.*;
import static io.art.core.model.CommunicatorActionIdentifier.*;
import static io.art.core.normalizer.ClassIdentifierNormalizer.*;
import static io.art.core.property.LazyProperty.*;
import static io.art.meta.module.MetaModule.*;
import static java.util.Objects.*;
import static java.util.function.UnaryOperator.*;
import java.util.*;
import java.util.function.*;

@RequiredArgsConstructor
public abstract class CommunicatorConfigurator {
    private final List<ClassBasedConfiguration> classBased = linkedList();

    public CommunicatorConfigurator configure(Class<? extends Communicator> communicatorClass) {
        return configure(() -> declaration(communicatorClass), identity());
    }

    public CommunicatorConfigurator configure(Class<? extends Communicator> communicatorClass, UnaryOperator<CommunicatorActionConfigurator> decorator) {
        return configure(() -> declaration(communicatorClass), decorator);
    }

    public CommunicatorConfigurator configure(Supplier<MetaClass<? extends Communicator>> communicatorClass, UnaryOperator<CommunicatorActionConfigurator> decorator) {
        classBased.add(new ClassBasedConfiguration(communicatorClass, decorator));
        return this;
    }

    protected LazyProperty<ImmutableMap<Class<?>, Communicator>> get(Supplier<CommunicatorConfiguration> configurationProvider, Supplier<? extends Communication> communication, LazyProperty<Set<Class<? extends Connector>>> connectors) {
        return lazy(() -> createProxies(configurationProvider, communication, connectors));
    }

    private ImmutableMap<Class<?>, Communicator> createProxies(Supplier<CommunicatorConfiguration> configurationProvider, Supplier<? extends Communication> communication, LazyProperty<Set<Class<? extends Connector>>> connectors) {
        ImmutableMap.Builder<Class<?>, Communicator> proxies = immutableMapBuilder();

        for (ClassBasedConfiguration registration : classBased) {
            registerMethods(proxies, registration.proxyClass.get(), registration.decorator);
        }

        for (Class<? extends Connector> connector : connectors.get()) {
            for (MetaMethod<?> method : declaration(connector).methods()) {
                registerMethods(proxies, cast(method.returnType().declaration()), UnaryOperator.identity());
            }
        }

        return proxies.build();
    }

    private void registerMethods(ImmutableMap.Builder<Class<?>, Communicator> builder, MetaClass<? extends Communicator> metaClass, UnaryOperator<CommunicatorActionConfigurator> decorator) {
        Communicator proxy = createCommunicatorProxy(metaClass, method -> createAction(metaClass, method, decorator)).getProxy();
        builder.put(metaClass.definition().type(), proxy);
    }

    private CommunicatorAction createAction(MetaClass<?> proxyClass, MetaMethod<?> metaMethod, UnaryOperator<CommunicatorActionConfigurator> decorator) {
        MetaType<?> inputType = orNull(() -> immutableArrayOf(metaMethod.parameters().values()).get(0).type(), isNotEmpty(metaMethod.parameters()));
        CommunicatorActionIdentifier id = communicatorActionId(asId(proxyClass.definition().type()), metaMethod.name());
        CommunicatorActionBuilder builder = CommunicatorAction.builder()
                .id(id)
                .outputType(metaMethod.returnType())
                .communication(communication.get());
        UnaryOperator<CommunicatorActionBuilder> configurator = decorator.apply(new CommunicatorActionConfigurator(id, configurationProvider.get())).configure();
        if (nonNull(inputType)) {
            return configurator.apply(builder.inputType(inputType)).build();
        }
        return configurator.apply(builder).build();
    }

    @RequiredArgsConstructor
    private static class ClassBasedConfiguration {
        final Supplier<MetaClass<? extends Communicator>> proxyClass;
        final UnaryOperator<CommunicatorActionConfigurator> decorator;
    }
}
