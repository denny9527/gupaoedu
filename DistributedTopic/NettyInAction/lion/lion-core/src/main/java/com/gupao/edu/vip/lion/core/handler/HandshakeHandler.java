
package com.gupao.edu.vip.lion.core.handler;

import com.google.common.base.Strings;
import com.gupao.edu.vip.lion.api.connection.Connection;
import com.gupao.edu.vip.lion.api.connection.SessionContext;
import com.gupao.edu.vip.lion.api.protocol.Packet;
import com.gupao.edu.vip.lion.common.handler.BaseMessageHandler;
import com.gupao.edu.vip.lion.common.message.ErrorMessage;
import com.gupao.edu.vip.lion.common.message.HandshakeMessage;
import com.gupao.edu.vip.lion.common.message.HandshakeOkMessage;
import com.gupao.edu.vip.lion.common.security.AesCipher;
import com.gupao.edu.vip.lion.common.security.CipherBox;
import com.gupao.edu.vip.lion.core.LionServer;
import com.gupao.edu.vip.lion.core.session.ReusableSession;
import com.gupao.edu.vip.lion.core.session.ReusableSessionManager;
import com.gupao.edu.vip.lion.tools.config.ConfigTools;
import com.gupao.edu.vip.lion.tools.log.Logs;

import static com.gupao.edu.vip.lion.common.ErrorCode.REPEAT_HANDSHAKE;


public final class HandshakeHandler extends BaseMessageHandler<HandshakeMessage> {

    private final ReusableSessionManager reusableSessionManager;

    public HandshakeHandler(LionServer lionServer) {
        this.reusableSessionManager = lionServer.getReusableSessionManager();
    }

    @Override
    public HandshakeMessage decode(Packet packet, Connection connection) {
        return new HandshakeMessage(packet, connection);
    }

    @Override
    public void handle(HandshakeMessage message) {
        if (message.getConnection().getSessionContext().isSecurity()) {
            doSecurity(message);
        } else {
            doInsecurity(message);
        }
    }

    private void doSecurity(HandshakeMessage message) {
        byte[] iv = message.iv;//AES密钥向量16位
        byte[] clientKey = message.clientKey;//客户端随机数16位
        byte[] serverKey = CipherBox.I.randomAESKey();//服务端随机数16位
        byte[] sessionKey = CipherBox.I.mixKey(clientKey, serverKey);//会话密钥16位

        //1.校验客户端消息字段
        if (Strings.isNullOrEmpty(message.deviceId)
                || iv.length != CipherBox.I.getAesKeyLength()
                || clientKey.length != CipherBox.I.getAesKeyLength()) {
            ErrorMessage.from(message).setReason("Param invalid").close();
            Logs.CONN.error("handshake failure, message={}, conn={}", message, message.getConnection());
            return;
        }

        //2.重复握手判断
        SessionContext context = message.getConnection().getSessionContext();
        if (message.deviceId.equals(context.deviceId)) {
            ErrorMessage.from(message).setErrorCode(REPEAT_HANDSHAKE).send();
            Logs.CONN.warn("handshake failure, repeat handshake, conn={}", message.getConnection());
            return;
        }

        //3.更换会话密钥RSA=>AES(clientKey)
        context.changeCipher(new AesCipher(clientKey, iv));

        //4.生成可复用session, 用于快速重连
        ReusableSession session = reusableSessionManager.genSession(context);

        //5.计算心跳时间
        int heartbeat = ConfigTools.getHeartbeat(message.minHeartbeat, message.maxHeartbeat);

        //6.响应握手成功消息
        HandshakeOkMessage
                .from(message)
                .setServerKey(serverKey)
                .setHeartbeat(heartbeat)
                .setSessionId(session.sessionId)
                .setExpireTime(session.expireTime)
                .send(f -> {
                            if (f.isSuccess()) {
                                //7.更换会话密钥AES(clientKey)=>AES(sessionKey)
                                context.changeCipher(new AesCipher(sessionKey, iv));
                                //8.保存client信息到当前连接
                                context.setOsName(message.osName)
                                        .setOsVersion(message.osVersion)
                                        .setClientVersion(message.clientVersion)
                                        .setDeviceId(message.deviceId)
                                        .setHeartbeat(heartbeat);

                                //9.保存可复用session到Redis, 用于快速重连
                                reusableSessionManager.cacheSession(session);

                                Logs.CONN.info("handshake success, conn={}", message.getConnection());
                            } else {
                                Logs.CONN.info("handshake failure, conn={}", message.getConnection(), f.cause());
                            }
                        }
                );
    }

    private void doInsecurity(HandshakeMessage message) {

        //1.校验客户端消息字段
        if (Strings.isNullOrEmpty(message.deviceId)) {
            ErrorMessage.from(message).setReason("Param invalid").close();
            Logs.CONN.error("handshake failure, message={}, conn={}", message, message.getConnection());
            return;
        }

        //2.重复握手判断
        SessionContext context = message.getConnection().getSessionContext();
        if (message.deviceId.equals(context.deviceId)) {
            ErrorMessage.from(message).setErrorCode(REPEAT_HANDSHAKE).send();
            Logs.CONN.warn("handshake failure, repeat handshake, conn={}", message.getConnection());
            return;
        }

        //6.响应握手成功消息
        HandshakeOkMessage.from(message).send();

        //8.保存client信息到当前连接
        context.setOsName(message.osName)
                .setOsVersion(message.osVersion)
                .setClientVersion(message.clientVersion)
                .setDeviceId(message.deviceId)
                .setHeartbeat(Integer.MAX_VALUE);

        Logs.CONN.info("handshake success, conn={}", message.getConnection());

    }
}