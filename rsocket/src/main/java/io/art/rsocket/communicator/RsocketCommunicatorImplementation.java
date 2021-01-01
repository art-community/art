/*
 * ART
 *
 * Copyright 2020 ART
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

package io.art.rsocket.communicator;

import io.art.communicator.implementation.*;
import io.art.rsocket.constants.RsocketModuleConstants.*;
import io.art.rsocket.payload.*;
import io.art.value.immutable.Value;
import io.rsocket.core.*;
import lombok.*;
import reactor.core.publisher.*;
import static io.art.core.caster.Caster.*;
import static io.art.core.checker.NullityChecker.*;
import static io.art.rsocket.module.RsocketModule.*;
import static io.art.value.constants.ValueConstants.*;
import static lombok.AccessLevel.*;

@Builder
public class RsocketCommunicatorImplementation implements CommunicatorImplementation {
    private final String connectorId;
    private final CommunicationMode communicationMode;
    private final DataFormat dataFormat;
    private final DataFormat metadataFormat;

    @Getter(lazy = true, value = PRIVATE)
    private final RsocketPayloadWriter writer = new RsocketPayloadWriter(getDataFormat(), getMetaDataFormat());

    @Getter(lazy = true, value = PRIVATE)
    private final RsocketPayloadReader reader = new RsocketPayloadReader(getDataFormat(), getMetaDataFormat());

    @Getter(lazy = true, value = PRIVATE)
    private final RSocketClient client = rsocketModule().state().getClient(connectorId).get();

    @Override
    public Flux<Value> communicate(Flux<Value> input) {
        RsocketPayloadWriter writer = getWriter();
        RsocketPayloadReader reader = getReader();
        switch (communicationMode) {
            case FIRE_AND_FORGET:
                return cast(getClient().fireAndForget(input.map(writer::writePayloadData).last()).flux());
            case REQUEST_RESPONSE:
                return getClient()
                        .requestResponse(input.map(writer::writePayloadData).last())
                        .flux()
                        .map(payload -> reader.readPayloadData(payload).getValue());
            case REQUEST_STREAM:
                return getClient()
                        .requestStream(input.map(writer::writePayloadData).last())
                        .map(payload -> reader.readPayloadData(payload).getValue());
            case REQUEST_CHANNEL:
                return getClient()
                        .requestChannel(input.map(writer::writePayloadData))
                        .map(payload -> reader.readPayloadData(payload).getValue());
            case METADATA_PUSH:
                return cast(getClient().metadataPush(input.map(writer::writePayloadData).last()).flux());
        }
        throw new IllegalStateException();
    }

    private DataFormat getDataFormat() {
        return orElse(dataFormat, rsocketModule().configuration().getCommunicatorConfiguration().getDefaultDataFormat());
    }

    private DataFormat getMetaDataFormat() {
        return orElse(metadataFormat, rsocketModule().configuration().getCommunicatorConfiguration().getDefaultMetaDataFormat());
    }
}
