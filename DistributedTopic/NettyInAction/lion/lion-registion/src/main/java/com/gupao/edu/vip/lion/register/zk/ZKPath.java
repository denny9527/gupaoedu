
package com.gupao.edu.vip.lion.register.zk;


import org.apache.curator.utils.ZKPaths;

import static org.apache.curator.utils.ZKPaths.PATH_SEPARATOR;

public enum ZKPath {
    REDIS_SERVER("/redis", "machine", "redis注册的地方"),
    CONNECT_SERVER("/cs/hosts", "machine", "connection server服务器应用注册的路径"),
    GATEWAY_SERVER("/gs/hosts", "machine", "gateway server服务器应用注册的路径"),
    WS_SERVER("/ws/hosts", "machine", "websocket server服务器应用注册的路径"),
    DNS_MAPPING("/dns/mapping", "machine", "dns mapping服务器应用注册的路径");

    ZKPath(String root, String name, String desc) {
        this.root = root;
        this.name = name;
    }

    private final String root;
    private final String name;

    public String getRootPath() {
        return root;
    }

    public String getNodePath() {
        return root + PATH_SEPARATOR + name;
    }

    public String getNodePath(String... tails) {
        String path = getNodePath();
        for (String tail : tails) {
            path += (PATH_SEPARATOR + tail);
        }
        return path;
    }

    //根据从zk中获取的app的值，拼装全路径
    public String getFullPath(String childPaths) {
        return root + PATH_SEPARATOR + childPaths;
    }

    public String getTail(String childPaths) {
        return ZKPaths.getNodeFromPath(childPaths);
    }

}
