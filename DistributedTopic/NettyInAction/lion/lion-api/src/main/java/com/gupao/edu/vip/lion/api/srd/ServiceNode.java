
package com.gupao.edu.vip.lion.api.srd;

/**
 *
 *
 */
public interface ServiceNode {

    String serviceName();

    String nodeId();

    String getHost();

    int getPort();

    default <T> T getAttr(String name) {
        return null;
    }

    default boolean isPersistent() {
        return false;
    }

    default String hostAndPort() {
        return getHost() + ":" + getPort();
    }

    default String nodePath() {
        return serviceName() + '/' + nodeId();
    }

}
