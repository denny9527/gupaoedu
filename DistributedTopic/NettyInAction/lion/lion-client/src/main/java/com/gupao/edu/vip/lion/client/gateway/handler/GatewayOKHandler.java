
package com.gupao.edu.vip.lion.client.gateway.handler;

import com.gupao.edu.vip.lion.api.connection.Connection;
import com.gupao.edu.vip.lion.api.protocol.Command;
import com.gupao.edu.vip.lion.api.protocol.Packet;
import com.gupao.edu.vip.lion.client.LionClient;
import com.gupao.edu.vip.lion.client.push.PushRequest;
import com.gupao.edu.vip.lion.client.push.PushRequestBus;
import com.gupao.edu.vip.lion.common.handler.BaseMessageHandler;
import com.gupao.edu.vip.lion.common.message.OkMessage;
import com.gupao.edu.vip.lion.common.push.GatewayPushResult;
import com.gupao.edu.vip.lion.tools.log.Logs;

/**
 *
 */
public final class GatewayOKHandler extends BaseMessageHandler<OkMessage> {

    private PushRequestBus pushRequestBus;

    public GatewayOKHandler(LionClient lionClient) {
        this.pushRequestBus = lionClient.getPushRequestBus();
    }

    @Override
    public OkMessage decode(Packet packet, Connection connection) {
        return new OkMessage(packet, connection);
    }

    @Override
    public void handle(OkMessage message) {
        if (message.cmd == Command.GATEWAY_PUSH.cmd) {
            PushRequest request = pushRequestBus.getAndRemove(message.getSessionId());
            if (request == null) {
                Logs.PUSH.warn("receive a gateway response, but request has timeout. message={}", message);
                return;
            }
            request.onSuccess(GatewayPushResult.fromJson(message.data));//推送成功
        }
    }
}
