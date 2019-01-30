
package com.gupao.edu.vip.lion.api.push;

import com.gupao.edu.vip.lion.api.LionContext;
import com.gupao.edu.vip.lion.api.service.Service;
import com.gupao.edu.vip.lion.api.spi.client.PusherFactory;

import java.util.concurrent.FutureTask;


public interface PushSender extends Service {

    /**
     * 创建PushSender实例
     *
     * @return PushSender
     */
    static PushSender create() {
        return PusherFactory.create();
    }

    /**
     * 推送push消息
     *
     * @param context 推送参数
     * @return FutureTask 可用于同步调用
     */
    FutureTask<PushResult> send(PushContext context);

    default FutureTask<PushResult> send(String context, String userId, PushCallback callback) {
        return send(PushContext
                .build(context)
                .setUserId(userId)
                .setCallback(callback)
        );
    }

    default FutureTask<PushResult> send(String context, String userId, AckModel ackModel, PushCallback callback) {
        return send(PushContext
                .build(context)
                .setAckModel(ackModel)
                .setUserId(userId)
                .setCallback(callback)
        );
    }

    default void setLionContext(LionContext context) {
    }
}
