package io.art.rsocket.test;

import io.art.meta.test.meta.*;
import io.art.rsocket.test.communicator.*;
import io.art.rsocket.test.meta.*;
import io.art.rsocket.test.meta.MetaRsocketTest.MetaIoPackage.MetaArtPackage.MetaRsocketPackage.MetaTestPackage.*;
import org.junit.jupiter.api.*;
import static io.art.core.initializer.Initializer.*;
import static io.art.json.module.JsonActivator.*;
import static io.art.logging.module.LoggingActivator.*;
import static io.art.meta.module.MetaActivator.*;
import static io.art.rsocket.Rsocket.*;
import static io.art.rsocket.module.RsocketActivator.*;

public class RsocketTest {
    public static MetaServicePackage services(MetaRsocketTest meta) {
        return meta.ioPackage().artPackage().rsocketPackage().testPackage().servicePackage();
    }

    @BeforeAll
    public static void setup() {
        initialize(
                meta(() -> new MetaRsocketTest(new MetaMetaTest())),
                logging(),
                json(),
                rsocket(rsocket -> rsocket
                        .communicator(communicator -> communicator
                                .tcp(TestRsocketConnector1.class)
                                .http(TestRsocketConnector2.class, http -> http.single(client -> client.common(builder -> builder.port(9001))))
                        )
                        .server(server -> server
                                .tcp()
                                .http(http -> http.common(common -> common.port(9001)))
                                .configurePackage(RsocketTest::services)
                        )
                )
        );
    }

    @Test
    public void test() {
        TestRsocketConnector1 tcp = rsocketConnector(TestRsocketConnector1.class);
        tcp.testRsocket1().m1("test");
    }
}
