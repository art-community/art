package io.art.communicator.factory;

import io.art.communicator.exception.*;
import io.art.communicator.model.*;
import io.art.meta.model.*;
import lombok.experimental.*;
import static io.art.communicator.constants.CommunicatorConstants.Errors.*;
import static io.art.core.caster.Caster.*;
import static io.art.core.collector.MapCollector.*;
import static io.art.core.constants.StringConstants.*;
import static java.text.MessageFormat.*;
import static java.util.Objects.*;
import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;
import java.util.*;
import java.util.function.*;

@UtilityClass
public class ConnectorProxyFactory {
    public static <T extends Connector> T createConnectorProxy(MetaClass<T> connectorClass, Function<Class<? extends Communicator>, ? extends Communicator> provider) {
        Map<MetaMethod<?>, MetaClass<?>> proxies = connectorClass.methods()
                .stream()
                .filter(method -> method.parameters().size() == 0)
                .filter(method -> nonNull(method.returnType().declaration()) && Connector.class.isAssignableFrom(method.returnType().type()))
                .collect(mapCollector(identity(), method -> method.returnType().declaration()));

        if (proxies.size() != connectorClass.methods().size()) {
            String invalidMethods = connectorClass.methods().stream()
                    .filter(method -> !proxies.containsKey(method))
                    .map(MetaMethod::toString)
                    .collect(joining(NEW_LINE));
            throw new CommunicatorException(format(CONNECTOR_HAS_INVALID_METHOD_FOR_PROXY, connectorClass.definition().type().getName(), invalidMethods));
        }

        Map<MetaMethod<?>, Function<Class<? extends Communicator>, ? extends Communicator>> invocations = proxies
                .entrySet()
                .stream()
                .collect(mapCollector(Map.Entry::getKey, entry -> ignore -> provider.apply(cast(entry.getKey().returnType().type()))));

        MetaProxy proxy = connectorClass.proxy(cast(invocations));

        if (isNull(proxy)) {
            throw new CommunicatorException(format(PROXY_IS_NULL, connectorClass.definition().type().getName()));
        }

        return cast(proxy);
    }
}