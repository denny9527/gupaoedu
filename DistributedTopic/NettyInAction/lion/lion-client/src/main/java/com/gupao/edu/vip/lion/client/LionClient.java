
package com.gupao.edu.vip.lion.client;

import com.gupao.edu.vip.lion.api.LionContext;
import com.gupao.edu.vip.lion.api.spi.common.*;
import com.gupao.edu.vip.lion.api.srd.ServiceDiscovery;
import com.gupao.edu.vip.lion.api.srd.ServiceRegistry;
import com.gupao.edu.vip.lion.client.gateway.connection.GatewayConnectionFactory;
import com.gupao.edu.vip.lion.client.push.PushRequestBus;
import com.gupao.edu.vip.lion.common.router.CachedRemoteRouterManager;
import com.gupao.edu.vip.lion.monitor.service.MonitorService;
import com.gupao.edu.vip.lion.monitor.service.ThreadPoolManager;
import com.gupao.edu.vip.lion.tools.event.EventBus;

/**
 *
 */
public final class LionClient implements LionContext {

    private MonitorService monitorService;

    private PushRequestBus pushRequestBus;

    private CachedRemoteRouterManager cachedRemoteRouterManager;

    private GatewayConnectionFactory gatewayConnectionFactory;

    public LionClient() {
        monitorService = new MonitorService();

        EventBus.create(monitorService.getThreadPoolManager().getEventBusExecutor());

        pushRequestBus = new PushRequestBus(this);

        cachedRemoteRouterManager = new CachedRemoteRouterManager();

        gatewayConnectionFactory = GatewayConnectionFactory.create(this);
    }

    public MonitorService getMonitorService() {
        return monitorService;
    }

    public ThreadPoolManager getThreadPoolManager() {
        return monitorService.getThreadPoolManager();
    }

    public PushRequestBus getPushRequestBus() {
        return pushRequestBus;
    }

    public CachedRemoteRouterManager getCachedRemoteRouterManager() {
        return cachedRemoteRouterManager;
    }

    public GatewayConnectionFactory getGatewayConnectionFactory() {
        return gatewayConnectionFactory;
    }

    @Override
    public MonitorService getMonitor() {
        return monitorService;
    }

    @Override
    public ServiceDiscovery getDiscovery() {
        return ServiceDiscoveryFactory.create();
    }

    @Override
    public ServiceRegistry getRegistry() {
        return ServiceRegistryFactory.create();
    }

    @Override
    public CacheManager getCacheManager() {
        return CacheManagerFactory.create();
    }

    @Override
    public MQClient getMQClient() {
        return MQClientFactory.create();
    }

}
