package io.art.tarantool.authenticator;

import io.netty.buffer.*;
import io.netty.channel.*;
import lombok.*;
import org.msgpack.value.Value;
import org.msgpack.value.*;
import static io.art.core.constants.AlgorithmConstants.*;
import static io.art.core.factory.MapFactory.*;
import static io.art.tarantool.constants.TarantoolModuleConstants.AuthenticationMechanism.*;
import static io.art.tarantool.constants.TarantoolModuleConstants.ProtocolConstants.*;
import static io.art.tarantool.descriptor.TarantoolRequestWriter.*;
import static io.art.tarantool.state.TarantoolRequestIdState.*;
import static org.msgpack.value.ValueFactory.*;
import java.util.*;

@RequiredArgsConstructor
public class TarantoolAuthenticationRequester extends SimpleChannelInboundHandler<ByteBuf> {
    private final String username;
    private final String password;

    @Override
    protected void channelRead0(ChannelHandlerContext context, ByteBuf input) {
        if (input.readableBytes() < GREETING_LENGTH) return;
        Value request = createAuthenticationRequest(input, username, password);
        context.channel().writeAndFlush(writeTarantoolRequest(authenticationId(), IPROTO_AUTH, request));
        context.pipeline().remove(this);
    }

    private static Value createAuthenticationRequest(ByteBuf input, String username, String password) {
        byte[] array = new byte[VERSION_LENGTH];
        input.readBytes(array);
        array = new byte[SALT_LENGTH];
        input.readBytes(array).skipBytes(VERSION_LENGTH - SALT_LENGTH);
        Map<IntegerValue, Value> request = map();
        request.put(newInteger(IPROTO_USER_NAME), newString(username));
        request.put(newInteger(IPROTO_AUTH_DATA), newArray(newString(CHAP_SHA1), newBinary(createPassword(array, password))));
        return newMap(request);
    }

    private static byte[] createPassword(byte[] serverAuthData, String password) {
        byte[] auth = SHA1_DIGEST.digest(password.getBytes());
        byte[] auth2 = SHA1_DIGEST.digest(auth);
        byte[] salt = Base64.getDecoder().decode(serverAuthData);
        SHA1_DIGEST.update(salt, 0, 20);
        SHA1_DIGEST.update(auth2);
        byte[] scramble = SHA1_DIGEST.digest();
        for (int i = 0; i < 20; i++) {
            auth[i] ^= scramble[i];
        }
        return auth;
    }
}