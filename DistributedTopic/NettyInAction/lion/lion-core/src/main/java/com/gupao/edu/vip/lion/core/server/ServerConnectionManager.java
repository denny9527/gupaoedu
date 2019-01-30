
package com.gupao.edu.vip.lion.core.server;


import com.gupao.edu.vip.lion.api.connection.Connection;
import com.gupao.edu.vip.lion.api.connection.ConnectionManager;
import com.gupao.edu.vip.lion.network.netty.connection.NettyConnection;
import com.gupao.edu.vip.lion.tools.config.CC;
import com.gupao.edu.vip.lion.tools.log.Logs;
import com.gupao.edu.vip.lion.tools.thread.NamedThreadFactory;
import com.gupao.edu.vip.lion.tools.thread.ThreadNames;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;



public final class ServerConnectionManager implements ConnectionManager {
    private final ConcurrentMap<ChannelId, ConnectionHolder> connections = new ConcurrentHashMap<>();
    private final ConnectionHolder DEFAULT = new SimpleConnectionHolder(null);
    private final boolean heartbeatCheck;
    private final ConnectionHolderFactory holderFactory;
    private HashedWheelTimer timer;

    public ServerConnectionManager(boolean heartbeatCheck) {
        this.heartbeatCheck = heartbeatCheck;
        this.holderFactory = heartbeatCheck ? HeartbeatCheckTask::new : SimpleConnectionHolder::new;
    }

    @Override
    public void init() {
        if (heartbeatCheck) {
            long tickDuration = TimeUnit.SECONDS.toMillis(1);//1s 每秒钟走一步，一个心跳周期内大致走一圈
            int ticksPerWheel = (int) (CC.lion.core.max_heartbeat / tickDuration);
            this.timer = new HashedWheelTimer(
                    new NamedThreadFactory(ThreadNames.T_CONN_TIMER),
                    tickDuration, TimeUnit.MILLISECONDS, ticksPerWheel
            );
        }
    }

    @Override
    public void destroy() {
        if (timer != null) {
            timer.stop();
        }
        connections.values().forEach(ConnectionHolder::close);
        connections.clear();
    }

    @Override
    public Connection get(Channel channel) {
        return connections.getOrDefault(channel.id(), DEFAULT).get();
    }

    @Override
    public void add(Connection connection) {
        connections.putIfAbsent(connection.getChannel().id(), holderFactory.create(connection));
    }

    @Override
    public Connection removeAndClose(Channel channel) {
        ConnectionHolder holder = connections.remove(channel.id());
        if (holder != null) {
            Connection connection = holder.get();
            holder.close();
            return connection;
        }

        //add default
        Connection connection = new NettyConnection();
        connection.init(channel, false);
        connection.close();
        return connection;
    }

    @Override
    public int getConnNum() {
        return connections.size();
    }

    private interface ConnectionHolder {
        Connection get();

        void close();
    }

    private static class SimpleConnectionHolder implements ConnectionHolder {
        private final Connection connection;

        private SimpleConnectionHolder(Connection connection) {
            this.connection = connection;
        }

        @Override
        public Connection get() {
            return connection;
        }

        @Override
        public void close() {
            if (connection != null) {
                connection.close();
            }
        }
    }


    private class HeartbeatCheckTask implements ConnectionHolder, TimerTask {

        private byte timeoutTimes = 0;
        private Connection connection;

        private HeartbeatCheckTask(Connection connection) {
            this.connection = connection;
            this.startTimeout();
        }

        void startTimeout() {
            Connection connection = this.connection;

            if (connection != null && connection.isConnected()) {
                int timeout = connection.getSessionContext().heartbeat;
                timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
            }
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            Connection connection = this.connection;

            if (connection == null || !connection.isConnected()) {
                Logs.HB.info("heartbeat timeout times={}, connection disconnected, conn={}", timeoutTimes, connection);
                return;
            }

            if (connection.isReadTimeout()) {
                if (++timeoutTimes > CC.lion.core.max_hb_timeout_times) {
                    connection.close();
                    Logs.HB.warn("client heartbeat timeout times={}, do close conn={}", timeoutTimes, connection);
                    return;
                } else {
                    Logs.HB.info("client heartbeat timeout times={}, connection={}", timeoutTimes, connection);
                }
            } else {
                timeoutTimes = 0;
            }
            startTimeout();
        }

        @Override
        public void close() {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        }

        @Override
        public Connection get() {
            return connection;
        }
    }

    @FunctionalInterface
    private interface ConnectionHolderFactory {
        ConnectionHolder create(Connection connection);
    }
}
