

package com.gupao.edu.vip.lion.core.handler;


import com.gupao.edu.vip.lion.api.connection.Connection;
import com.gupao.edu.vip.lion.api.message.MessageHandler;
import com.gupao.edu.vip.lion.api.protocol.Packet;
import com.gupao.edu.vip.lion.api.spi.Spi;
import com.gupao.edu.vip.lion.api.spi.handler.PushHandlerFactory;
import com.gupao.edu.vip.lion.common.handler.BaseMessageHandler;
import com.gupao.edu.vip.lion.common.message.AckMessage;
import com.gupao.edu.vip.lion.common.message.PushMessage;
import com.gupao.edu.vip.lion.tools.log.Logs;


@Spi(order = 1)
public final class ClientPushHandler extends BaseMessageHandler<PushMessage> implements PushHandlerFactory {

    @Override
    public PushMessage decode(Packet packet, Connection connection) {
        return new PushMessage(packet, connection);
    }

    @Override
    public void handle(PushMessage message) {
        Logs.PUSH.info("receive client push message={}", message);

        if (message.autoAck()) {
            AckMessage.from(message).sendRaw();
            Logs.PUSH.info("send ack for push message={}", message);
        }
        //biz code write here
    }

    @Override
    public MessageHandler get() {
        return this;
    }
}
