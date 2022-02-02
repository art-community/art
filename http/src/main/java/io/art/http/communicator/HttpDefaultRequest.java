package io.art.http.communicator;

import io.art.core.annotation.*;
import io.art.meta.model.*;
import io.art.transport.constants.TransportModuleConstants.*;
import io.art.transport.payload.*;
import io.netty.buffer.*;
import lombok.*;
import reactor.core.publisher.*;
import static io.art.core.extensions.FileExtensions.*;
import static io.art.core.extensions.NettyBufferExtensions.*;
import static io.art.meta.Meta.*;
import static io.art.transport.constants.TransportModuleConstants.DataFormat.*;
import static io.art.transport.payload.TransportPayloadWriter.*;
import static lombok.AccessLevel.*;
import java.io.*;
import java.nio.file.*;

@Public
@Getter(value = PACKAGE)
@RequiredArgsConstructor(access = PACKAGE)
public class HttpDefaultRequest {
    private final byte[] input;

    public static HttpDefaultRequest json(Object value) {
        return create(value, JSON);
    }

    public static HttpDefaultRequest yaml(Object value) {
        return create(value, YAML);
    }

    public static HttpDefaultRequest messagePack(Object value) {
        return create(value, MESSAGE_PACK);
    }

    public static HttpDefaultRequest string(String value) {
        return create(value, STRING);
    }

    public static HttpDefaultRequest bytes(byte[] value) {
        return create(value, BYTES);
    }

    public static HttpDefaultRequest buffer(ByteBuf value) {
        return create(releaseToByteArray(value), BYTES);
    }

    public static HttpDefaultRequest file(File file) {
        return create(readFileBytes(file.toPath()), BYTES);
    }

    public static HttpDefaultRequest path(Path path) {
        return create(readFileBytes(path), BYTES);
    }

    private static HttpDefaultRequest create(Object value, DataFormat dataFormat) {
        TransportPayloadWriter writer = transportPayloadWriter(dataFormat);
        byte[] bytes = releaseToByteArray(writer.write(new TypedObject(definition(value.getClass()), value)));
        return new HttpDefaultRequest(bytes);
    }
}