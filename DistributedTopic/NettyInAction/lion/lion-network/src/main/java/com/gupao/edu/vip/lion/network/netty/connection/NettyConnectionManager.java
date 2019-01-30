
package com.gupao.edu.vip.lion.network.netty.connection;

import com.gupao.edu.vip.lion.api.connection.Connection;
import com.gupao.edu.vip.lion.api.connection.ConnectionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class NettyConnectionManager implements ConnectionManager {
    private final ConcurrentMap<ChannelId, Connection> connections = new ConcurrentHashMap<>();

    @Override
    public Connection get(Channel channel) {
        return connections.get(channel.id());
    }

    @Override
    public Connection removeAndClose(Channel channel) {
        return connections.remove(channel.id());
    }

    @Override
    public void add(Connection connection) {
        connections.putIfAbsent(connection.getChannel().id(), connection);
    }

    @Override
    public int getConnNum() {
        return connections.size();
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {
        connections.values().forEach(Connection::close);
        connections.clear();
    }
}
