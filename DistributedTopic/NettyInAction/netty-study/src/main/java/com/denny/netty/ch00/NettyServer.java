package com.denny.netty.ch00;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.LineEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class NettyServer {

    private static final String IP = "127.0.0.1";

    private static final int PORT = 6666;

    private static final int BIZ_GROUP_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    private static final int BIZ_Thread_SIZE = 100;

    private static final EventLoopGroup boosGroup = new NioEventLoopGroup(BIZ_GROUP_SIZE);

    private static final EventLoopGroup workGroup  = new NioEventLoopGroup(BIZ_Thread_SIZE);

    public static void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    //.handler(ChannelHandler) 处理服务端(ServerSocketChannel)逻辑，如channel注册等
                    .childHandler(new ChannelInitializer<Channel>() {//对连接上的处理(SocketChannel)

                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            //pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            //pipeline.addLast(new LengthFieldPrepender(4));
                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new TcpServerHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(IP, PORT).sync();//Server Channel绑定注册同步阻塞，开始接受连接

            channelFuture.channel().closeFuture().sync();//Server Channel关闭同步阻塞

            log.info("server start");
        }catch (Exception e) {
            log.info("启动服务端失败！");
        }finally {
            shutdown();
        }

    }

    protected static void shutdown(){
        //关闭线程组
        boosGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        log.info("启动Server");
        NettyServer.start();
    }
}
