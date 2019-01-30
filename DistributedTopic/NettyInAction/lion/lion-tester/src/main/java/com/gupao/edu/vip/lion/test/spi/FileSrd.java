
package com.gupao.edu.vip.lion.test.spi;

import com.google.common.collect.Lists;
import com.gupao.edu.vip.lion.api.service.BaseService;
import com.gupao.edu.vip.lion.api.service.Listener;
import com.gupao.edu.vip.lion.api.srd.*;
import com.gupao.edu.vip.lion.tools.Jsons;
import com.gupao.edu.vip.lion.tools.log.Logs;

import java.util.List;

/**
 */
public final class FileSrd extends BaseService implements ServiceRegistry, ServiceDiscovery {

    public static final FileSrd I = new FileSrd();

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
    public void init() {
        Logs.Console.warn("你正在使用的ServiceRegistry和ServiceDiscovery只能用于源码测试，生产环境请使用zookeeper.");
    }

    @Override
    public void register(ServiceNode node) {
        FileCacheManger.I.hset(node.serviceName(), node.nodeId(), Jsons.toJson(node));
    }

    @Override
    public void deregister(ServiceNode node) {
        FileCacheManger.I.hdel(node.serviceName(), node.nodeId());
    }

    @Override
    public List<ServiceNode> lookup(String path) {
        return Lists.newArrayList(FileCacheManger.I.hgetAll(path, CommonServiceNode.class).values());
    }

    @Override
    public void subscribe(String path, ServiceListener listener) {

    }

    @Override
    public void unsubscribe(String path, ServiceListener listener) {

    }
}
