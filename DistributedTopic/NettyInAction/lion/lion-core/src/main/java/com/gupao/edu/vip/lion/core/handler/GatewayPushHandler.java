
package com.gupao.edu.vip.lion.core.handler;

import com.gupao.edu.vip.lion.api.connection.Connection;
import com.gupao.edu.vip.lion.api.protocol.Packet;
import com.gupao.edu.vip.lion.common.handler.BaseMessageHandler;
import com.gupao.edu.vip.lion.common.message.gateway.GatewayPushMessage;
import com.gupao.edu.vip.lion.core.push.PushCenter;


public final class GatewayPushHandler extends BaseMessageHandler<GatewayPushMessage> {

    private final PushCenter pushCenter;

    public GatewayPushHandler(PushCenter pushCenter) {
        this.pushCenter = pushCenter;
    }

    @Override
    public GatewayPushMessage decode(Packet packet, Connection connection) {
        return new GatewayPushMessage(packet, connection);
    }

    @Override
    public void handle(GatewayPushMessage message) {
        pushCenter.push(message);
    }
}
