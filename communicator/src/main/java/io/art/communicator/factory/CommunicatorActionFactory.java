package io.art.communicator.factory;

import io.art.communicator.action.*;
import io.art.communicator.action.CommunicatorAction.*;
import io.art.communicator.configuration.*;
import io.art.communicator.decorator.*;
import io.art.communicator.model.*;
import io.art.communicator.refresher.*;
import io.art.core.model.*;
import io.art.meta.model.*;
import lombok.experimental.*;
import static io.art.communicator.configuration.CommunicatorConfiguration.*;
import static io.art.core.checker.EmptinessChecker.*;
import static io.art.core.checker.ModuleChecker.*;
import static io.art.core.checker.NullityChecker.*;
import static io.art.core.constants.MethodDecoratorScope.*;
import static io.art.core.factory.ArrayFactory.*;
import static io.art.core.model.CommunicatorActionIdentifier.*;
import static io.art.core.normalizer.ClassIdentifierNormalizer.*;
import static java.util.Objects.*;

@UtilityClass
public class CommunicatorActionFactory {
    public CommunicatorAction communicatorAction(MetaClass<?> owner, MetaMethod<?> method, Communication communication) {
        return communicatorAction(communicatorActionId(idByDash(owner.definition().type()), method.name()), method, communication);
    }

    public CommunicatorAction communicatorAction(CommunicatorActionIdentifier id, MetaMethod<?> method, Communication communication) {
        MetaType<?> inputType = orNull(() -> immutableArrayOf(method.parameters().values()).get(0).type(), isNotEmpty(method.parameters()));
        CommunicatorActionBuilder builder = CommunicatorAction.builder()
                .id(id)
                .outputType(method.returnType())
                .communication(communication);
        if (nonNull(inputType)) {
            return builder.inputType(inputType).build();
        }
        return builder.build();
    }

    public CommunicatorAction preconfiguredCommunicatorAction(MetaClass<?> owner, MetaMethod<?> method, Communication communication) {
        return preconfiguredCommunicatorAction(communicatorActionId(idByDash(owner.definition().type()), method.name()), method, communication);
    }

    public CommunicatorAction preconfiguredCommunicatorAction(CommunicatorActionIdentifier id, MetaMethod<?> method, Communication communication) {
        MetaType<?> inputType = orNull(() -> immutableArrayOf(method.parameters().values()).get(0).type(), isNotEmpty(method.parameters()));
        CommunicatorConfiguration configuration = communicatorConfiguration(new CommunicatorRefresher());
        CommunicatorActionBuilder builder = CommunicatorAction.builder()
                .id(id)
                .outputType(method.returnType())
                .communication(communication)
                .inputDecorator(new CommunicatorDeactivationDecorator(id, configuration));
        if (withLogging()) {
            builder
                    .inputDecorator(new CommunicatorLoggingDecorator(id, configuration, INPUT))
                    .outputDecorator(new CommunicatorLoggingDecorator(id, configuration, OUTPUT));
        }
        if (nonNull(inputType)) {
            return builder.inputType(inputType).build();
        }
        return builder.build();
    }
}
