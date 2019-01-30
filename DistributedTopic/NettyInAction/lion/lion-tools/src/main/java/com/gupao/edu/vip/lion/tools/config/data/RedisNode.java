
package com.gupao.edu.vip.lion.tools.config.data;

/**
 */
public class RedisNode {
    public String host;
    public int port;

    public RedisNode() {
    }

    public RedisNode(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public static RedisNode from(String config) {
        String[] array = config.split(":");
        if (array.length == 2) {
            return new RedisNode(array[0], Integer.parseInt(array[1]));
        } else {
            return new RedisNode(array[0], Integer.parseInt(array[1]));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RedisNode server = (RedisNode) o;

        if (port != server.port) return false;
        return host.equals(server.host);

    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return "RedisServer{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
