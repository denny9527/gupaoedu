
package com.gupao.edu.vip.lion.client.push;

import com.gupao.edu.vip.lion.api.LionContext;
import com.gupao.edu.vip.lion.api.push.PushContext;
import com.gupao.edu.vip.lion.api.push.PushException;
import com.gupao.edu.vip.lion.api.push.PushResult;
import com.gupao.edu.vip.lion.api.push.PushSender;
import com.gupao.edu.vip.lion.api.service.BaseService;
import com.gupao.edu.vip.lion.api.service.Listener;
import com.gupao.edu.vip.lion.api.spi.common.CacheManagerFactory;
import com.gupao.edu.vip.lion.api.spi.common.ServiceDiscoveryFactory;
import com.gupao.edu.vip.lion.client.LionClient;
import com.gupao.edu.vip.lion.client.gateway.connection.GatewayConnectionFactory;
import com.gupao.edu.vip.lion.common.router.CachedRemoteRouterManager;
import com.gupao.edu.vip.lion.common.router.RemoteRouter;

import java.util.Set;
import java.util.concurrent.FutureTask;

public final class PushClient extends BaseService implements PushSender {

    private LionClient lionClient;

    private PushRequestBus pushRequestBus;

    private CachedRemoteRouterManager cachedRemoteRouterManager;

    private GatewayConnectionFactory gatewayConnectionFactory;

    private FutureTask<PushResult> send0(PushContext ctx) {
        if (ctx.isBroadcast()) {
            return PushRequest.build(lionClient, ctx).broadcast();
        } else {
            Set<RemoteRouter> remoteRouters = cachedRemoteRouterManager.lookupAll(ctx.getUserId());
            if (remoteRouters == null || remoteRouters.isEmpty()) {
                return PushRequest.build(lionClient, ctx).onOffline();
            }
            FutureTask<PushResult> task = null;
            for (RemoteRouter remoteRouter : remoteRouters) {
                task = PushRequest.build(lionClient, ctx).send(remoteRouter);
            }
            return task;
        }
    }

    @Override
    public FutureTask<PushResult> send(PushContext ctx) {
        if (ctx.isBroadcast()) {
            return send0(ctx.setUserId(null));
        } else if (ctx.getUserId() != null) {
            return send0(ctx);
        } else if (ctx.getUserIds() != null) {
            FutureTask<PushResult> task = null;
            for (String userId : ctx.getUserIds()) {
                task = send0(ctx.setUserId(userId));
            }
            return task;
        } else {
            throw new PushException("param error.");
        }
    }

    @Override
    protected void doStart(Listener listener) throws Throwable {
        if (lionClient == null) {
            lionClient = new LionClient();
        }

        pushRequestBus = lionClient.getPushRequestBus();
        cachedRemoteRouterManager = lionClient.getCachedRemoteRouterManager();
        gatewayConnectionFactory = lionClient.getGatewayConnectionFactory();

        ServiceDiscoveryFactory.create().syncStart();
        CacheManagerFactory.create().init();
        pushRequestBus.syncStart();
        gatewayConnectionFactory.start(listener);
    }

    @Override
    protected void doStop(Listener listener) throws Throwable {
        ServiceDiscoveryFactory.create().syncStop();
        CacheManagerFactory.create().destroy();
        pushRequestBus.syncStop();
        gatewayConnectionFactory.stop(listener);
    }

    @Override
    public boolean isRunning() {
        return started.get();
    }

    @Override
    public void setLionContext(LionContext context) {
        this.lionClient = ((LionClient) context);
    }
}
