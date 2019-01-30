
package com.gupao.edu.vip.lion.core;

import com.gupao.edu.vip.lion.api.LionContext;
import com.gupao.edu.vip.lion.api.spi.common.*;
import com.gupao.edu.vip.lion.api.srd.ServiceDiscovery;
import com.gupao.edu.vip.lion.api.srd.ServiceNode;
import com.gupao.edu.vip.lion.api.srd.ServiceRegistry;
import com.gupao.edu.vip.lion.common.ServerNodes;
import com.gupao.edu.vip.lion.core.push.PushCenter;
import com.gupao.edu.vip.lion.core.router.RouterCenter;
import com.gupao.edu.vip.lion.core.server.*;
import com.gupao.edu.vip.lion.core.session.ReusableSessionManager;
import com.gupao.edu.vip.lion.monitor.service.MonitorService;
import com.gupao.edu.vip.lion.network.netty.http.HttpClient;
import com.gupao.edu.vip.lion.network.netty.http.NettyHttpClient;
import com.gupao.edu.vip.lion.tools.event.EventBus;

import static com.gupao.edu.vip.lion.tools.config.CC.lion.net.tcpGateway;

public final class LionServer implements LionContext {

    private ServiceNode connServerNode;
    private ServiceNode gatewayServerNode;
    private ServiceNode websocketServerNode;

    private ConnectionServer connectionServer;
    private WebsocketServer websocketServer;
    private GatewayServer gatewayServer;
    private AdminServer adminServer;
    private GatewayUDPConnector udpGatewayServer;

    private HttpClient httpClient;

    private PushCenter pushCenter;

    private ReusableSessionManager reusableSessionManager;

    private RouterCenter routerCenter;

    private MonitorService monitorService;


    public LionServer() {
        connServerNode = ServerNodes.cs();
        gatewayServerNode = ServerNodes.gs();
        websocketServerNode = ServerNodes.ws();

        monitorService = new MonitorService();
        EventBus.create(monitorService.getThreadPoolManager().getEventBusExecutor());

        reusableSessionManager = new ReusableSessionManager();

        pushCenter = new PushCenter(this);

        routerCenter = new RouterCenter(this);

        connectionServer = new ConnectionServer(this);

        websocketServer = new WebsocketServer(this);

        adminServer = new AdminServer(this);

        if (tcpGateway()) {
            gatewayServer = new GatewayServer(this);
        } else {
            udpGatewayServer = new GatewayUDPConnector(this);
        }
    }

    public boolean isTargetMachine(String host, int port) {
        return port == gatewayServerNode.getPort() && gatewayServerNode.getHost().equals(host);
    }

    public ServiceNode getConnServerNode() {
        return connServerNode;
    }

    public ServiceNode getGatewayServerNode() {
        return gatewayServerNode;
    }

    public ServiceNode getWebsocketServerNode() {
        return websocketServerNode;
    }

    public ConnectionServer getConnectionServer() {
        return connectionServer;
    }

    public GatewayServer getGatewayServer() {
        return gatewayServer;
    }

    public AdminServer getAdminServer() {
        return adminServer;
    }

    public GatewayUDPConnector getUdpGatewayServer() {
        return udpGatewayServer;
    }

    public WebsocketServer getWebsocketServer() {
        return websocketServer;
    }

    public HttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (this) {
                if (httpClient == null) {
                    httpClient = new NettyHttpClient();
                }
            }
        }
        return httpClient;
    }

    public PushCenter getPushCenter() {
        return pushCenter;
    }

    public ReusableSessionManager getReusableSessionManager() {
        return reusableSessionManager;
    }

    public RouterCenter getRouterCenter() {
        return routerCenter;
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
