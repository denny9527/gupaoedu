
package com.gupao.edu.vip.lion.test.zk;

import com.gupao.edu.vip.lion.api.spi.common.ServiceDiscoveryFactory;
import com.gupao.edu.vip.lion.api.spi.common.ServiceRegistryFactory;
import com.gupao.edu.vip.lion.api.srd.*;
import com.gupao.edu.vip.lion.common.ServerNodes;
import com.gupao.edu.vip.lion.register.zk.ZKClient;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.locks.LockSupport;

import static org.apache.curator.utils.ZKPaths.PATH_SEPARATOR;

/**
 */
public final class ZKClientTest {
    public static void main(String[] args) {
        ServiceRegistry registry = ServiceRegistryFactory.create();
        registry.syncStart();

        registry.register(ServerNodes.gs());
        registry.register(ServerNodes.gs());
        registry.deregister(ServerNodes.gs());
        LockSupport.park();
    }

    @Test
    public void testDiscovery() {
        ServiceDiscovery discovery = ServiceDiscoveryFactory.create();
        discovery.syncStart();

        System.err.println(discovery.lookup(ServiceNames.GATEWAY_SERVER));
        discovery.subscribe(ServiceNames.GATEWAY_SERVER, new ServiceListener() {
            @Override
            public void onServiceAdded(String path, ServiceNode node) {
                System.err.println(path + "," + node);
                System.err.println(discovery.lookup(ServiceNames.GATEWAY_SERVER));
            }

            @Override
            public void onServiceUpdated(String path, ServiceNode node) {
                System.err.println(path + "," + node);
                System.err.println(discovery.lookup(ServiceNames.GATEWAY_SERVER));
            }

            @Override
            public void onServiceRemoved(String path, ServiceNode node) {
                System.err.println(path + "," + node);
                System.err.println(discovery.lookup(ServiceNames.GATEWAY_SERVER));
            }
        });
        LockSupport.park();
    }

    @Test
    public void testZK() throws Exception {
        ZKClient.I.syncStart();
        ZKClient.I.registerEphemeral(ServerNodes.gs().serviceName(), "3");
        ZKClient.I.registerEphemeral(ServerNodes.gs().serviceName(), "4");
        System.err.println("==================" + ZKClient.I.getChildrenKeys(ServiceNames.GATEWAY_SERVER));
        List<String> rawData = ZKClient.I.getChildrenKeys(ServiceNames.GATEWAY_SERVER);
        if (rawData == null || rawData.isEmpty()) {
            return;
        }
        for (String raw : rawData) {
            String fullPath = ServiceNames.GATEWAY_SERVER + PATH_SEPARATOR + raw;
            System.err.println("==================" + ZKClient.I.get(fullPath));
        }

    }
}
