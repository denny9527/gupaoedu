
package com.gupao.edu.vip.lion.client.gateway;

import com.gupao.edu.vip.lion.api.connection.ConnectionManager;
import com.gupao.edu.vip.lion.api.protocol.Command;
import com.gupao.edu.vip.lion.api.service.Listener;
import com.gupao.edu.vip.lion.client.LionClient;
import com.gupao.edu.vip.lion.client.gateway.handler.GatewayClientChannelHandler;
import com.gupao.edu.vip.lion.client.gateway.handler.GatewayErrorHandler;
import com.gupao.edu.vip.lion.client.gateway.handler.GatewayOKHandler;
import com.gupao.edu.vip.lion.common.MessageDispatcher;
import com.gupao.edu.vip.lion.network.netty.client.NettyTCPClient;
import com.gupao.edu.vip.lion.network.netty.connection.NettyConnectionManager;
import com.gupao.edu.vip.lion.tools.config.CC;
import com.gupao.edu.vip.lion.tools.config.CC.lion.net.rcv_buf;
import com.gupao.edu.vip.lion.tools.config.CC.lion.net.snd_buf;
import com.gupao.edu.vip.lion.tools.thread.NamedPoolThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.sctp.nio.NioSctpChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;

import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.gupao.edu.vip.lion.tools.config.CC.lion.net.traffic_shaping.gateway_client.*;
import static com.gupao.edu.vip.lion.tools.thread.ThreadNames.T_TRAFFIC_SHAPING;

/**
 */
public class GatewayClient extends NettyTCPClient {
    private final GatewayClientChannelHandler handler;
    private GlobalChannelTrafficShapingHandler trafficShapingHandler;
    private ScheduledExecutorService trafficShapingExecutor;
    private final ConnectionManager connectionManager;
    private final MessageDispatcher messageDispatcher;

    public GatewayClient(LionClient lionClient) {
        messageDispatcher = new MessageDispatcher();
        messageDispatcher.register(Command.OK, () -> new GatewayOKHandler(lionClient));
        messageDispatcher.register(Command.ERROR, () -> new GatewayErrorHandler(lionClient));
        connectionManager = new NettyConnectionManager();
        handler = new GatewayClientChannelHandler(connectionManager, messageDispatcher);
        if (enabled) {
            trafficShapingExecutor = Executors.newSingleThreadScheduledExecutor(new NamedPoolThreadFactory(T_TRAFFIC_SHAPING));
            trafficShapingHandler = new GlobalChannelTrafficShapingHandler(
                    trafficShapingExecutor,
                    write_global_limit, read_global_limit,
                    write_channel_limit, read_channel_limit,
                    check_interval);
        }
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return handler;
    }

    @Override
    protected void initPipeline(ChannelPipeline pipeline) {
        super.initPipeline(pipeline);
        if (trafficShapingHandler != null) {
            pipeline.addFirst(trafficShapingHandler);
        }
    }

    @Override
    protected void doStop(Listener listener) throws Throwable {
        if (trafficShapingHandler != null) {
            trafficShapingHandler.release();
            trafficShapingExecutor.shutdown();
        }
        super.doStop(listener);
    }

    @Override
    protected void initOptions(Bootstrap b) {
        super.initOptions(b);
        if (snd_buf.gateway_client > 0) b.option(ChannelOption.SO_SNDBUF, snd_buf.gateway_client);
        if (rcv_buf.gateway_client > 0) b.option(ChannelOption.SO_RCVBUF, rcv_buf.gateway_client);
    }

    @Override
    public ChannelFactory<? extends Channel> getChannelFactory() {
        if (CC.lion.net.tcpGateway()) return super.getChannelFactory();
        if (CC.lion.net.udtGateway()) return NioUdtProvider.BYTE_CONNECTOR;
        if (CC.lion.net.sctpGateway()) return NioSctpChannel::new;
        return super.getChannelFactory();
    }

    @Override
    public SelectorProvider getSelectorProvider() {
        if (CC.lion.net.tcpGateway()) return super.getSelectorProvider();
        if (CC.lion.net.udtGateway()) return NioUdtProvider.BYTE_PROVIDER;
        if (CC.lion.net.sctpGateway()) return super.getSelectorProvider();
        return super.getSelectorProvider();
    }

    @Override
    protected int getWorkThreadNum() {
        return CC.lion.thread.pool.gateway_client_work;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public MessageDispatcher getMessageDispatcher() {
        return messageDispatcher;
    }
}
