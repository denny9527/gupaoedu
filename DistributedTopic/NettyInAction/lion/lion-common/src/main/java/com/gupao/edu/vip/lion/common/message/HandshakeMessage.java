
package com.gupao.edu.vip.lion.common.message;

import com.gupao.edu.vip.lion.api.connection.Cipher;
import com.gupao.edu.vip.lion.api.connection.Connection;
import com.gupao.edu.vip.lion.api.protocol.Packet;
import com.gupao.edu.vip.lion.api.spi.core.RsaCipherFactory;
import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.Map;

import static com.gupao.edu.vip.lion.api.protocol.Command.HANDSHAKE;

/**
 */
public final class HandshakeMessage extends ByteBufMessage {
    public String deviceId;
    public String osName;
    public String osVersion;
    public String clientVersion;
    public byte[] iv;
    public byte[] clientKey;
    public int minHeartbeat;
    public int maxHeartbeat;
    public long timestamp;

    public HandshakeMessage(Connection connection) {
        super(new Packet(HANDSHAKE, genSessionId()), connection);
    }

    public HandshakeMessage(Packet message, Connection connection) {
        super(message, connection);
    }

    @Override
    public void decode(ByteBuf body) {
        deviceId = decodeString(body);
        osName = decodeString(body);
        osVersion = decodeString(body);
        clientVersion = decodeString(body);
        iv = decodeBytes(body);
        clientKey = decodeBytes(body);
        minHeartbeat = decodeInt(body);
        maxHeartbeat = decodeInt(body);
        timestamp = decodeLong(body);
    }

    public void encode(ByteBuf body) {
        encodeString(body, deviceId);
        encodeString(body, osName);
        encodeString(body, osVersion);
        encodeString(body, clientVersion);
        encodeBytes(body, iv);
        encodeBytes(body, clientKey);
        encodeInt(body, minHeartbeat);
        encodeInt(body, maxHeartbeat);
        encodeLong(body, timestamp);
    }

    @Override
    public void decodeJsonBody(Map<String, Object> body) {
        deviceId = (String) body.get("deviceId");
        osName = (String) body.get("osName");
        osVersion = (String) body.get("osVersion");
        clientVersion = (String) body.get("clientVersion");
    }

    @Override
    protected Cipher getCipher() {
        return RsaCipherFactory.create();
    }

    @Override
    public String toString() {
        return "HandshakeMessage{" +
                "clientKey=" + Arrays.toString(clientKey) +
                ", deviceId='" + deviceId + '\'' +
                ", osName='" + osName + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", clientVersion='" + clientVersion + '\'' +
                ", iv=" + Arrays.toString(iv) +
                ", minHeartbeat=" + minHeartbeat +
                ", maxHeartbeat=" + maxHeartbeat +
                ", timestamp=" + timestamp +
                ", packet=" + packet +
                '}';
    }
}
