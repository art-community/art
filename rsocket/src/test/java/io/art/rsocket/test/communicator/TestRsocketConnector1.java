package io.art.rsocket.test.communicator;

import io.art.core.communication.*;

public interface TestRsocketConnector1 extends Connector {
    TestRsocket testRsocket();

    TestRsocket1 testRsocket1();
}
