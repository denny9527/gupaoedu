
package com.gupao.edu.vip.lion.register.zk;

import com.gupao.edu.vip.lion.api.service.BaseService;
import com.gupao.edu.vip.lion.api.service.Listener;
import com.gupao.edu.vip.lion.api.srd.*;
import com.gupao.edu.vip.lion.tools.Jsons;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.curator.utils.ZKPaths.PATH_SEPARATOR;

/**
 */
public final class ZKServiceRegistryAndDiscovery extends BaseService implements ServiceRegistry, ServiceDiscovery {

    public static final ZKServiceRegistryAndDiscovery I = new ZKServiceRegistryAndDiscovery();

    private final ZKClient client;

    public ZKServiceRegistryAndDiscovery() {
        this.client = ZKClient.I;
    }

    @Override
    public void start(Listener listener) {
        if (isRunning()) {
            listener.onSuccess();
        } else {
            super.start(listener);
        }
    }

    @Override
    public void stop(Listener listener) {
        if (isRunning()) {
            super.stop(listener);
        } else {
            listener.onSuccess();
        }
    }

    @Override
    protected void doStart(Listener listener) throws Throwable {
        client.start(listener);
    }

    @Override
    protected void doStop(Listener listener) throws Throwable {
        client.stop(listener);
    }

    @Override
    public void register(ServiceNode node) {
        if (node.isPersistent()) {
            client.registerPersist(node.nodePath(), Jsons.toJson(node));
        } else {
            client.registerEphemeral(node.nodePath(), Jsons.toJson(node));
        }
    }

    @Override
    public void deregister(ServiceNode node) {
        if (client.isRunning()) {
            client.remove(node.nodePath());
        }
    }

    @Override
    public List<ServiceNode> lookup(String serviceName) {
        List<String> childrenKeys = client.getChildrenKeys(serviceName);
        if (childrenKeys == null || childrenKeys.isEmpty()) {
            return Collections.emptyList();
        }

        return childrenKeys.stream()
                .map(key -> serviceName + PATH_SEPARATOR + key)
                .map(client::get)
                .filter(Objects::nonNull)
                .map(childData -> Jsons.fromJson(childData, CommonServiceNode.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void subscribe(String watchPath, ServiceListener listener) {
        client.registerListener(new ZKCacheListener(watchPath, listener));
    }

    @Override
    public void unsubscribe(String path, ServiceListener listener) {

    }
}
