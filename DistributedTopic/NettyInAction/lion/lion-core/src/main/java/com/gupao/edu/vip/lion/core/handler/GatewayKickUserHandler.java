
package com.gupao.edu.vip.lion.core.handler;

import com.gupao.edu.vip.lion.api.connection.Connection;
import com.gupao.edu.vip.lion.api.protocol.Packet;
import com.gupao.edu.vip.lion.common.handler.BaseMessageHandler;
import com.gupao.edu.vip.lion.common.message.gateway.GatewayKickUserMessage;
import com.gupao.edu.vip.lion.core.router.RouterCenter;


public final class GatewayKickUserHandler extends BaseMessageHandler<GatewayKickUserMessage> {

    private final RouterCenter routerCenter;

    public GatewayKickUserHandler(RouterCenter routerCenter) {
        this.routerCenter = routerCenter;
    }

    @Override
    public GatewayKickUserMessage decode(Packet packet, Connection connection) {
        return new GatewayKickUserMessage(packet, connection);
    }

    @Override
    public void handle(GatewayKickUserMessage message) {
        routerCenter.getRouterChangeListener().onReceiveKickRemoteMsg(message);
    }
}
