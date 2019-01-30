
package com.gupao.edu.vip.lion.core.mq;

import com.gupao.edu.vip.lion.api.LionContext;
import com.gupao.edu.vip.lion.api.spi.Spi;
import com.gupao.edu.vip.lion.api.spi.push.PushListener;
import com.gupao.edu.vip.lion.api.spi.push.PushListenerFactory;
import com.gupao.edu.vip.lion.core.LionServer;


@Spi(order = 2)
public final class MQPushListener implements PushListener<MQPushMessage>, PushListenerFactory<MQPushMessage> {
    private final MQClient mqClient = new MQClient();

    @Override
    public void init(LionContext context) {
        mqClient.init();
        MQMessageReceiver.subscribe(mqClient, ((LionServer) context).getPushCenter());
    }


    @Override
    public void onSuccess(MQPushMessage message, Object[] timePoints) {
        //publish messageId to mq:[success/queue]
        mqClient.publish("/lion/push/success", message);
    }

    @Override
    public void onAckSuccess(MQPushMessage message, Object[] timePoints) {
        //publish messageId to mq:[success/queue]
        mqClient.publish("/lion/push/success", message);
    }

    @Override
    public void onBroadcastComplete(MQPushMessage message, Object[] timePoints) {
        //publish messageId to mq:[broadcast/finish/queue]
        mqClient.publish("/lion/push/broadcast_finish", message);
    }

    @Override
    public void onFailure(MQPushMessage message, Object[] timePoints) {
        //publish messageId to mq:[failure/queue], client can retry
        mqClient.publish("/lion/push/failure", message);
    }

    @Override
    public void onOffline(MQPushMessage message, Object[] timePoints) {
        //publish messageId to mq:[offline/queue], client persist offline message to db
        mqClient.publish("/lion/push/offline", message);
    }

    @Override
    public void onRedirect(MQPushMessage message, Object[] timePoints) {
        //publish messageId to mq:[route/change/queue], client should be try again
        mqClient.publish("/lion/push/route_change", message);
    }

    @Override
    public void onTimeout(MQPushMessage message, Object[] timePoints) {
        //publish messageId to mq:[ack/timeout/queue], client can retry
        mqClient.publish("/lion/push/ack_timeout", message);
    }

    @Override
    public PushListener<MQPushMessage> get() {
        return this;
    }
}
