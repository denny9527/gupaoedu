
package com.gupao.edu.vip.lion.core.handler;

import com.google.common.base.Strings;
import com.gupao.edu.vip.lion.api.connection.Connection;
import com.gupao.edu.vip.lion.api.connection.SessionContext;
import com.gupao.edu.vip.lion.api.event.UserOfflineEvent;
import com.gupao.edu.vip.lion.api.event.UserOnlineEvent;
import com.gupao.edu.vip.lion.api.protocol.Command;
import com.gupao.edu.vip.lion.api.protocol.Packet;
import com.gupao.edu.vip.lion.api.spi.Spi;
import com.gupao.edu.vip.lion.api.spi.handler.BindValidator;
import com.gupao.edu.vip.lion.api.spi.handler.BindValidatorFactory;
import com.gupao.edu.vip.lion.common.handler.BaseMessageHandler;
import com.gupao.edu.vip.lion.common.message.BindUserMessage;
import com.gupao.edu.vip.lion.common.message.ErrorMessage;
import com.gupao.edu.vip.lion.common.message.OkMessage;
import com.gupao.edu.vip.lion.common.router.RemoteRouter;
import com.gupao.edu.vip.lion.common.router.RemoteRouterManager;
import com.gupao.edu.vip.lion.core.LionServer;
import com.gupao.edu.vip.lion.core.router.LocalRouter;
import com.gupao.edu.vip.lion.core.router.LocalRouterManager;
import com.gupao.edu.vip.lion.core.router.RouterCenter;
import com.gupao.edu.vip.lion.tools.event.EventBus;
import com.gupao.edu.vip.lion.tools.log.Logs;


public final class BindUserHandler extends BaseMessageHandler<BindUserMessage> {
    private final BindValidator bindValidator = BindValidatorFactory.create();

    private RouterCenter routerCenter;

    public BindUserHandler(LionServer lionServer) {
        this.routerCenter = lionServer.getRouterCenter();
        this.bindValidator.init(lionServer);
    }

    @Override
    public BindUserMessage decode(Packet packet, Connection connection) {
        return new BindUserMessage(packet, connection);
    }

    @Override
    public void handle(BindUserMessage message) {
        if (message.getPacket().cmd == Command.BIND.cmd) {
            bind(message);
        } else {
            unbind(message);
        }
    }

    private void bind(BindUserMessage message) {
        if (Strings.isNullOrEmpty(message.userId)) {
            ErrorMessage.from(message).setReason("invalid param").close();
            Logs.CONN.error("bind user failure for invalid param, conn={}", message.getConnection());
            return;
        }
        //1.绑定用户时先看下是否握手成功
        SessionContext context = message.getConnection().getSessionContext();
        if (context.handshakeOk()) {
            //处理重复绑定问题
            if (context.userId != null) {
                if (message.userId.equals(context.userId)) {
                    context.tags = message.tags;
                    OkMessage.from(message).setData("bind success").sendRaw();
                    Logs.CONN.info("rebind user success, userId={}, session={}", message.userId, context);
                    return;
                } else {
                    unbind(message);
                }
            }

            //验证用户身份
            boolean success = bindValidator.validate(message.userId, message.data);
            if (success) {
                //2.如果握手成功，就把用户链接信息注册到路由中心，本地和远程各一份
                success = routerCenter.register(message.userId, message.getConnection());
            }

            if (success) {
                context.userId = message.userId;
                context.tags = message.tags;
                EventBus.post(new UserOnlineEvent(message.getConnection(), message.userId));
                OkMessage.from(message).setData("bind success").sendRaw();
                Logs.CONN.info("bind user success, userId={}, session={}", message.userId, context);
            } else {
                //3.注册失败再处理下，防止本地注册成功，远程注册失败的情况，只有都成功了才叫成功
                routerCenter.unRegister(message.userId, context.getClientType());
                ErrorMessage.from(message).setReason("bind failed").close();
                Logs.CONN.info("bind user failure, userId={}, session={}", message.userId, context);
            }
        } else {
            ErrorMessage.from(message).setReason("not handshake").close();
            Logs.CONN.error("bind user failure not handshake, userId={}, conn={}", message.userId, message.getConnection());
        }
    }

    /**
     * 目前是以用户维度来存储路由信息的，所以在删除路由信息时要判断下是否是同一个设备
     * 后续可以修改为按设备来存储路由信息。
     *
     * @param message
     */
    private void unbind(BindUserMessage message) {
        if (Strings.isNullOrEmpty(message.userId)) {
            ErrorMessage.from(message).setReason("invalid param").close();
            Logs.CONN.error("unbind user failure invalid param, session={}", message.getConnection().getSessionContext());
            return;
        }
        //1.解绑用户时先看下是否握手成功
        SessionContext context = message.getConnection().getSessionContext();
        if (context.handshakeOk()) {
            //2.先删除远程路由, 必须是同一个设备才允许解绑
            boolean unRegisterSuccess = true;
            int clientType = context.getClientType();
            String userId = context.userId;
            RemoteRouterManager remoteRouterManager = routerCenter.getRemoteRouterManager();
            RemoteRouter remoteRouter = remoteRouterManager.lookup(userId, clientType);
            if (remoteRouter != null) {
                String deviceId = remoteRouter.getRouteValue().getDeviceId();
                if (context.deviceId.equals(deviceId)) {//判断是否是同一个设备
                    unRegisterSuccess = remoteRouterManager.unRegister(userId, clientType);
                }
            }
            //3.删除本地路由信息
            LocalRouterManager localRouterManager = routerCenter.getLocalRouterManager();
            LocalRouter localRouter = localRouterManager.lookup(userId, clientType);
            if (localRouter != null) {
                String deviceId = localRouter.getRouteValue().getSessionContext().deviceId;
                if (context.deviceId.equals(deviceId)) {//判断是否是同一个设备
                    unRegisterSuccess = localRouterManager.unRegister(userId, clientType) && unRegisterSuccess;
                }
            }

            //4.路由删除成功，广播用户下线事件
            if (unRegisterSuccess) {
                context.userId = null;
                context.tags = null;
                EventBus.post(new UserOfflineEvent(message.getConnection(), userId));
                OkMessage.from(message).setData("unbind success").sendRaw();
                Logs.CONN.info("unbind user success, userId={}, session={}", userId, context);
            } else {
                ErrorMessage.from(message).setReason("unbind failed").sendRaw();
                Logs.CONN.error("unbind user failure, unRegister router failure, userId={}, session={}", userId, context);
            }
        } else {
            ErrorMessage.from(message).setReason("not handshake").close();
            Logs.CONN.error("unbind user failure not handshake, userId={}, session={}", message.userId, context);
        }
    }


    @Spi(order = 1)
    public static class DefaultBindValidatorFactory implements BindValidatorFactory {
        private final BindValidator validator = (userId, data) -> true;

        @Override
        public BindValidator get() {
            return validator;
        }
    }
}
