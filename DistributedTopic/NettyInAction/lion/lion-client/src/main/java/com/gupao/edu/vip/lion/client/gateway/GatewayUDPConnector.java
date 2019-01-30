
package com.gupao.edu.vip.lion.client.gateway;

import com.gupao.edu.vip.lion.api.connection.Connection;
import com.gupao.edu.vip.lion.api.protocol.Command;
import com.gupao.edu.vip.lion.api.service.Listener;
import com.gupao.edu.vip.lion.client.LionClient;
import com.gupao.edu.vip.lion.client.gateway.handler.GatewayErrorHandler;
import com.gupao.edu.vip.lion.client.gateway.handler.GatewayOKHandler;
import com.gupao.edu.vip.lion.common.MessageDispatcher;
import com.gupao.edu.vip.lion.network.netty.udp.UDPChannelHandler;
import com.gupao.edu.vip.lion.network.netty.udp.NettyUDPConnector;
import com.gupao.edu.vip.lion.tools.Utils;
import com.gupao.edu.vip.lion.tools.config.CC;
import com.gupao.edu.vip.lion.tools.config.CC.lion.net.rcv_buf;
import com.gupao.edu.vip.lion.tools.config.CC.lion.net.snd_buf;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;

import static com.gupao.edu.vip.lion.common.MessageDispatcher.POLICY_LOG;

/**
 */
public final class GatewayUDPConnector extends NettyUDPConnector {

    private UDPChannelHandler channelHandler;
    private MessageDispatcher messageDispatcher;
    private LionClient lionClient;

    public GatewayUDPConnector(LionClient lionClient) {
        super(CC.lion.net.gateway_client_port);
        this.lionClient = lionClient;
        this.messageDispatcher = new MessageDispatcher(POLICY_LOG);
    }

    @Override
    public void init() {
        super.init();
        messageDispatcher.register(Command.OK, () -> new GatewayOKHandler(lionClient));
        messageDispatcher.register(Command.ERROR, () -> new GatewayErrorHandler(lionClient));
        channelHandler = new UDPChannelHandler(messageDispatcher);
        channelHandler.setMulticastAddress(Utils.getInetAddress(CC.lion.net.gateway_client_multicast));
        channelHandler.setNetworkInterface(Utils.getLocalNetworkInterface());
    }


    @Override
    public void stop(Listener listener) {
        super.stop(listener);
    }


    @Override
    protected void initOptions(Bootstrap b) {
        super.initOptions(b);
        b.option(ChannelOption.IP_MULTICAST_LOOP_DISABLED, true);
        b.option(ChannelOption.IP_MULTICAST_TTL, 255);
        if (snd_buf.gateway_client > 0) b.option(ChannelOption.SO_SNDBUF, snd_buf.gateway_client);
        if (rcv_buf.gateway_client > 0) b.option(ChannelOption.SO_RCVBUF, rcv_buf.gateway_client);
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }

    public Connection getConnection() {
        return channelHandler.getConnection();
    }

    public MessageDispatcher getMessageDispatcher() {
        return messageDispatcher;
    }
}
