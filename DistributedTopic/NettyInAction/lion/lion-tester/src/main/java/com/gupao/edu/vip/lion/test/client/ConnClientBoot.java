
package com.gupao.edu.vip.lion.test.client;

import com.gupao.edu.vip.lion.api.service.BaseService;
import com.gupao.edu.vip.lion.api.service.Listener;
import com.gupao.edu.vip.lion.api.spi.common.CacheManagerFactory;
import com.gupao.edu.vip.lion.api.spi.common.ServiceDiscoveryFactory;
import com.gupao.edu.vip.lion.api.srd.ServiceNames;
import com.gupao.edu.vip.lion.api.srd.ServiceNode;
import com.gupao.edu.vip.lion.client.connect.ClientConfig;
import com.gupao.edu.vip.lion.client.connect.ConnClientChannelHandler;
import com.gupao.edu.vip.lion.monitor.service.MonitorService;
import com.gupao.edu.vip.lion.network.netty.codec.PacketDecoder;
import com.gupao.edu.vip.lion.network.netty.codec.PacketEncoder;
import com.gupao.edu.vip.lion.tools.event.EventBus;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

import static com.gupao.edu.vip.lion.client.connect.ConnClientChannelHandler.CONFIG_KEY;

public final class ConnClientBoot extends BaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnClientBoot.class);

    private Bootstrap bootstrap;
    private NioEventLoopGroup workerGroup;
    private MonitorService monitorService;


    @Override
    protected void doStart(Listener listener) throws Throwable {
        ServiceDiscoveryFactory.create().syncStart();
        CacheManagerFactory.create().init();
        monitorService = new MonitorService();
        EventBus.create(monitorService.getThreadPoolManager().getEventBusExecutor());

        this.workerGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)//
                .option(ChannelOption.TCP_NODELAY, true)//
                .option(ChannelOption.SO_REUSEADDR, true)//
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)//
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60 * 1000)
                .option(ChannelOption.SO_RCVBUF, 5 * 1024 * 1024)
                .channel(NioSocketChannel.class);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() { // (4)
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("decoder", new PacketDecoder());
                ch.pipeline().addLast("encoder", PacketEncoder.INSTANCE);
                ch.pipeline().addLast("handler", new ConnClientChannelHandler());
            }
        });

        listener.onSuccess();
    }

    @Override
    protected void doStop(Listener listener) throws Throwable {
        if (workerGroup != null) workerGroup.shutdownGracefully();
        ServiceDiscoveryFactory.create().syncStop();
        CacheManagerFactory.create().destroy();
        listener.onSuccess();
    }

    public List<ServiceNode> getServers() {
        return ServiceDiscoveryFactory.create().lookup(ServiceNames.CONN_SERVER);
    }

    public ChannelFuture connect(InetSocketAddress remote, InetSocketAddress local, ClientConfig clientConfig) {
        ChannelFuture future = local != null ? bootstrap.connect(remote, local) : bootstrap.connect(remote);
        if (future.channel() != null) future.channel().attr(CONFIG_KEY).set(clientConfig);
        future.addListener(f -> {
            if (f.isSuccess()) {
                future.channel().attr(CONFIG_KEY).set(clientConfig);
                LOGGER.info("start netty client success, remote={}, local={}", remote, local);
            } else {
                LOGGER.error("start netty client failure, remote={}, local={}", remote, local, f.cause());
            }
        });
        return future;
    }

    public ChannelFuture connect(String host, int port, ClientConfig clientConfig) {
        return connect(new InetSocketAddress(host, port), null, clientConfig);
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public NioEventLoopGroup getWorkerGroup() {
        return workerGroup;
    }
}