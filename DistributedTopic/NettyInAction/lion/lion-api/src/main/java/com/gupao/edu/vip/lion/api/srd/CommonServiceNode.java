
package com.gupao.edu.vip.lion.api.srd;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 */
public final class CommonServiceNode implements ServiceNode {

    private String host;

    private int port;

    private Map<String, Object> attrs;

    private transient String name;

    private transient String nodeId;

    private transient boolean persistent;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public void setServiceName(String name) {
        this.name = name;
    }

    public CommonServiceNode addAttr(String name, Object value) {
        if (attrs == null) attrs = new HashMap<>();
        attrs.put(name, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttr(String name) {
        if (attrs == null || attrs.isEmpty()) {
            return null;
        }
        return (T) attrs.get(name);
    }

    @Override
    public boolean isPersistent() {
        return persistent;
    }

    @Override
    public String hostAndPort() {
        return host + ":" + port;
    }

    @Override
    public String serviceName() {
        return name;
    }

    @Override
    public String nodeId() {
        if (nodeId == null) {
            nodeId = UUID.randomUUID().toString();
        }
        return nodeId;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
    }

    @Override
    public String toString() {
        return "{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", attrs=" + attrs +
                ", persistent=" + persistent +
                '}';
    }
}
