

package com.gupao.edu.vip.lion.network.netty.udp;

import com.gupao.edu.vip.lion.api.connection.Connection;
import com.gupao.edu.vip.lion.api.message.PacketReceiver;
import com.gupao.edu.vip.lion.api.protocol.Packet;
import com.gupao.edu.vip.lion.network.netty.codec.PacketDecoder;
import com.gupao.edu.vip.lion.network.netty.connection.NettyConnection;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;


@ChannelHandler.Sharable
public final class UDPChannelHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(UDPChannelHandler.class);
    private final NettyConnection connection = new NettyConnection();
    private final PacketReceiver receiver;
    private InetAddress multicastAddress;
    private NetworkInterface networkInterface;

    public UDPChannelHandler(PacketReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        connection.init(ctx.channel(), false);
        if (multicastAddress != null) {
            ((DatagramChannel) ctx.channel()).joinGroup(multicastAddress, networkInterface, null).addListener(future -> {
                if (future.isSuccess()) {
                    LOGGER.info("join multicast group success, channel={}, group={}", ctx.channel(), multicastAddress);
                } else {
                    LOGGER.error("join multicast group error, channel={}, group={}", ctx.channel(), multicastAddress, future.cause());
                }
            });
        }
        LOGGER.info("init udp channel={}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        connection.close();
        if (multicastAddress != null) {
            ((DatagramChannel) ctx.channel()).leaveGroup(multicastAddress, networkInterface, null).addListener(future -> {
                if (future.isSuccess()) {
                    LOGGER.info("leave multicast group success, channel={}, group={}", ctx.channel(), multicastAddress);
                } else {
                    LOGGER.error("leave multicast group error, channel={}, group={}", ctx.channel(), multicastAddress, future.cause());
                }
            });
        }
        LOGGER.info("disconnect udp channel={}, connection={}", ctx.channel(), connection);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DatagramPacket datagramPacket = (DatagramPacket) msg;
        Packet packet = PacketDecoder.decodeFrame(datagramPacket);
        receiver.onReceive(packet, connection);
        datagramPacket.release();//最后一个使用方要释放引用
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        connection.close();
        LOGGER.error("udp handler caught an exception, channel={}, conn={}", ctx.channel(), connection, cause);
    }

    public UDPChannelHandler setMulticastAddress(InetAddress multicastAddress) {
        if (!multicastAddress.isMulticastAddress()) {
            throw new IllegalArgumentException(multicastAddress + "not a multicastAddress");
        }

        this.multicastAddress = multicastAddress;
        return this;
    }

    public UDPChannelHandler setNetworkInterface(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
        return this;
    }

    public Connection getConnection() {
        return connection;
    }
}
