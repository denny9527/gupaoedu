
package com.gupao.edu.vip.lion.common.message;

import com.gupao.edu.vip.lion.api.connection.Connection;
import com.gupao.edu.vip.lion.api.protocol.JsonPacket;
import com.gupao.edu.vip.lion.api.protocol.Packet;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import static com.gupao.edu.vip.lion.api.protocol.Command.KICK;

/**
 */
public class KickUserMessage extends ByteBufMessage {
    public String deviceId;
    public String userId;

    public KickUserMessage(Packet message, Connection connection) {
        super(message, connection);
    }

    public static KickUserMessage build(Connection connection) {
        if (connection.getSessionContext().isSecurity()) {
            return new KickUserMessage(new Packet(KICK), connection);
        } else {
            return new KickUserMessage(new JsonPacket(KICK), connection);
        }
    }

    @Override
    public void decode(ByteBuf body) {
        deviceId = decodeString(body);
        userId = decodeString(body);
    }

    @Override
    public void encode(ByteBuf body) {
        encodeString(body, deviceId);
        encodeString(body, userId);
    }

    @Override
    protected Map<String, Object> encodeJsonBody() {
        Map<String, Object> body = new HashMap<>(2);
        body.put("deviceId", deviceId);
        body.put("userId", userId);
        return body;
    }

    @Override
    public String toString() {
        return "KickUserMessage{" +
                "deviceId='" + deviceId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
