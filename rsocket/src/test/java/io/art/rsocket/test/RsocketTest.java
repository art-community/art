package io.art.rsocket.test;

import io.art.meta.test.meta.*;
import io.art.rsocket.test.communicator.*;
import io.art.rsocket.test.meta.*;
import io.art.rsocket.test.service.*;
import org.junit.jupiter.api.*;
import static io.art.core.initializer.Initializer.*;
import static io.art.core.model.CommunicatorActionIdentifier.*;
import static io.art.json.module.JsonActivator.*;
import static io.art.logging.module.LoggingActivator.*;
import static io.art.meta.module.MetaActivator.*;
import static io.art.rsocket.Rsocket.*;
import static io.art.rsocket.module.RsocketActivator.*;

public class RsocketTest {
    @BeforeAll
    public static void setup() {
        initialize(
                meta(() -> new MetaRsocketTest(new MetaMetaTest())),
                logging(),
                json(),
                rsocket(rsocket -> rsocket
                        .communicator(communicator -> communicator
                                .tcp(tcp -> tcp
                                        .configure(common -> common.logging(true))
                                        .forClass(TestRsocketOtherCommunicator.class, action -> action.target(communicatorId(TestRsocketCommunicator.class)))
                                        .forClass(TestRsocketCommunicator.class)))
                        .server(server -> server
                                .tcp(tcp -> tcp.common(common -> common.logging(true)))
                                .forClass(TestRsocketService.class))
                )
        );
    }

    @Test
    public void test() {
        TestRsocketCommunicator communicator = rsocketCommunicator(TestRsocketCommunicator.class);
        communicator.m("test");
    }
}
