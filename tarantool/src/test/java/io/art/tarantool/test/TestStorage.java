package io.art.tarantool.test;

import io.art.communicator.*;
import io.art.tarantool.test.model.*;

public interface TestStorage extends Connector {
    TestSpace testSpace();

    interface TestSpace extends Communicator {
        TestRequest tryRequest(TestRequest request);
    }
}
