package io.art.tarantool.factory;

import io.art.tarantool.communicator.*;
import io.art.tarantool.configuration.*;
import io.art.tarantool.storage.*;
import lombok.experimental.*;
import static io.art.tarantool.module.TarantoolModule.*;

@UtilityClass
public class TarantoolCommunicationFactory {
    public static TarantoolFunctionCommunication createTarantoolCommunication(TarantoolStorageConfiguration storageConfiguration) {
        String storage = storageConfiguration.getStorage();
        TarantoolModuleConfiguration moduleConfiguration = tarantoolModule().configuration();
        return new TarantoolFunctionCommunication(moduleConfiguration.getStorages().get(storage), moduleConfiguration);
    }
}
