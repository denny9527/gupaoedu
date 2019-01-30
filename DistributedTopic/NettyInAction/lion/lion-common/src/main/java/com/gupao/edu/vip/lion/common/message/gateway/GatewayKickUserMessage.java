
package com.gupao.edu.vip.lion.common.message.gateway;

import com.gupao.edu.vip.lion.api.connection.Connection;
import com.gupao.edu.vip.lion.api.protocol.Packet;
import com.gupao.edu.vip.lion.common.memory.PacketFactory;
import com.gupao.edu.vip.lion.common.message.ByteBufMessage;
import com.gupao.edu.vip.lion.common.router.KickRemoteMsg;
import io.netty.buffer.ByteBuf;

import static com.gupao.edu.vip.lion.api.protocol.Command.GATEWAY_KICK;

/**
 */
public final class GatewayKickUserMessage extends ByteBufMessage implements KickRemoteMsg {
    public String userId;
    public String deviceId;
    public String connId;
    public int clientType;
    public String targetServer;
    public int targetPort;


    public GatewayKickUserMessage(Packet message, Connection connection) {
        super(message, connection);
    }

    public static GatewayKickUserMessage build(Connection connection) {
        Packet packet = PacketFactory.get(GATEWAY_KICK);
        packet.sessionId = genSessionId();
        return new GatewayKickUserMessage(packet, connection);
    }

    @Override
    public void decode(ByteBuf body) {
        userId = decodeString(body);
        deviceId = decodeString(body);
        connId = decodeString(body);
        clientType = decodeInt(body);
        targetServer = decodeString(body);
        targetPort = decodeInt(body);
    }

    @Override
    public void encode(ByteBuf body) {
        encodeString(body, userId);
        encodeString(body, deviceId);
        encodeString(body, connId);
        encodeInt(body, clientType);
        encodeString(body, targetServer);
        encodeInt(body, targetPort);
    }

    public GatewayKickUserMessage setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public GatewayKickUserMessage setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public GatewayKickUserMessage setConnId(String connId) {
        this.connId = connId;
        return this;
    }

    public GatewayKickUserMessage setClientType(int clientType) {
        this.clientType = clientType;
        return this;
    }

    public GatewayKickUserMessage setTargetServer(String targetServer) {
        this.targetServer = targetServer;
        return this;
    }

    public GatewayKickUserMessage setTargetPort(int targetPort) {
        this.targetPort = targetPort;
        return this;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public String getConnId() {
        return connId;
    }

    @Override
    public int getClientType() {
        return clientType;
    }

    @Override
    public String getTargetServer() {
        return targetServer;
    }

    @Override
    public int getTargetPort() {
        return targetPort;
    }

    @Override
    public String toString() {
        return "GatewayKickUserMessage{" +
                "userId='" + userId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", connId='" + connId + '\'' +
                ", clientType=" + clientType +
                ", targetServer='" + targetServer + '\'' +
                ", targetPort=" + targetPort +
                '}';
    }
}
