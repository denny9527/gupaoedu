
package com.gupao.edu.vip.lion.core.push;

import com.gupao.edu.vip.lion.api.LionContext;
import com.gupao.edu.vip.lion.api.spi.Spi;
import com.gupao.edu.vip.lion.api.spi.push.PushListener;
import com.gupao.edu.vip.lion.api.spi.push.PushListenerFactory;
import com.gupao.edu.vip.lion.common.message.ErrorMessage;
import com.gupao.edu.vip.lion.common.message.OkMessage;
import com.gupao.edu.vip.lion.common.message.gateway.GatewayPushMessage;
import com.gupao.edu.vip.lion.core.LionServer;
import com.gupao.edu.vip.lion.tools.Jsons;
import com.gupao.edu.vip.lion.tools.log.Logs;

import java.util.concurrent.ScheduledExecutorService;

import static com.gupao.edu.vip.lion.common.ErrorCode.*;
import static com.gupao.edu.vip.lion.common.push.GatewayPushResult.toJson;


@Spi(order = 1)
public final class GatewayPushListener implements PushListener<GatewayPushMessage>, PushListenerFactory<GatewayPushMessage> {

    private PushCenter pushCenter;

    @Override
    public void init(LionContext context) {
        pushCenter = ((LionServer) context).getPushCenter();
    }

    @Override
    public void onSuccess(GatewayPushMessage message, Object[] timePoints) {
        if (message.getConnection().isConnected()) {
            pushCenter.addTask(new PushTask() {
                @Override
                public ScheduledExecutorService getExecutor() {
                    return message.getExecutor();
                }

                @Override
                public void run() {
                    OkMessage
                            .from(message)
                            .setData(toJson(message, timePoints))
                            .sendRaw();
                }
            });
        } else {
            Logs.PUSH.warn("push message to client success, but gateway connection is closed, timePoints={}, message={}"
                    , Jsons.toJson(timePoints), message);
        }
    }

    @Override
    public void onAckSuccess(GatewayPushMessage message, Object[] timePoints) {
        if (message.getConnection().isConnected()) {
            pushCenter.addTask(new PushTask() {
                @Override
                public ScheduledExecutorService getExecutor() {
                    return message.getExecutor();
                }

                @Override
                public void run() {
                    OkMessage
                            .from(message)
                            .setData(toJson(message, timePoints))
                            .sendRaw();
                }
            });

        } else {
            Logs.PUSH.warn("client ack success, but gateway connection is closed, timePoints={}, message={}"
                    , Jsons.toJson(timePoints), message);
        }
    }

    @Override
    public void onBroadcastComplete(GatewayPushMessage message, Object[] timePoints) {
        if (message.getConnection().isConnected()) {
            pushCenter.addTask(new PushTask() {
                @Override
                public ScheduledExecutorService getExecutor() {
                    return message.getExecutor();
                }

                @Override
                public void run() {
                    OkMessage
                            .from(message)
                            .sendRaw();
                }
            });
        } else {
            Logs.PUSH.warn("broadcast to client finish, but gateway connection is closed, timePoints={}, message={}"
                    , Jsons.toJson(timePoints), message);
        }
    }

    @Override
    public void onFailure(GatewayPushMessage message, Object[] timePoints) {
        if (message.getConnection().isConnected()) {
            pushCenter.addTask(new PushTask() {
                @Override
                public ScheduledExecutorService getExecutor() {
                    return message.getExecutor();
                }

                @Override
                public void run() {
                    ErrorMessage
                            .from(message)
                            .setErrorCode(PUSH_CLIENT_FAILURE)
                            .setData(toJson(message, timePoints))
                            .sendRaw();
                }
            });
        } else {
            Logs.PUSH.warn("push message to client failure, but gateway connection is closed, timePoints={}, message={}"
                    , Jsons.toJson(timePoints), message);
        }
    }

    @Override
    public void onOffline(GatewayPushMessage message, Object[] timePoints) {
        if (message.getConnection().isConnected()) {
            pushCenter.addTask(new PushTask() {
                @Override
                public ScheduledExecutorService getExecutor() {
                    return message.getExecutor();
                }

                @Override
                public void run() {
                    ErrorMessage
                            .from(message)
                            .setErrorCode(OFFLINE)
                            .setData(toJson(message, timePoints))
                            .sendRaw();
                }
            });
        } else {
            Logs.PUSH.warn("push message to client offline, but gateway connection is closed, timePoints={}, message={}"
                    , Jsons.toJson(timePoints), message);
        }
    }

    @Override
    public void onRedirect(GatewayPushMessage message, Object[] timePoints) {
        if (message.getConnection().isConnected()) {
            pushCenter.addTask(new PushTask() {
                @Override
                public ScheduledExecutorService getExecutor() {
                    return message.getExecutor();
                }

                @Override
                public void run() {
                    ErrorMessage
                            .from(message)
                            .setErrorCode(ROUTER_CHANGE)
                            .setData(toJson(message, timePoints))
                            .sendRaw();
                }
            });
        } else {
            Logs.PUSH.warn("push message to client redirect, but gateway connection is closed, timePoints={}, message={}"
                    , Jsons.toJson(timePoints), message);
        }
    }


    @Override
    public void onTimeout(GatewayPushMessage message, Object[] timePoints) {
        Logs.PUSH.warn("push message to client timeout, timePoints={}, message={}"
                , Jsons.toJson(timePoints), message);
    }

    @Override
    public PushListener<GatewayPushMessage> get() {
        return this;
    }
}
