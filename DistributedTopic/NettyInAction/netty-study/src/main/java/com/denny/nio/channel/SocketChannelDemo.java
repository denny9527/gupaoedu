package com.denny.nio.channel;

import com.denny.nio.buffer.Buffers;
import io.netty.channel.WriteBufferWaterMark;
import jdk.management.resource.internal.inst.SocketChannelImplRMHooks;
import lombok.extern.slf4j.Slf4j;
import sun.reflect.generics.scope.Scope;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

@Slf4j
public class SocketChannelDemo {

    public static class TCPEchoClient implements Runnable {

        private String name;

        private InetSocketAddress remoteAddress;

        public TCPEchoClient(String name, InetSocketAddress remoteAddress) {
            this.name = name;
            this.remoteAddress = remoteAddress;
        }

        @Override
        public void run() {

            Charset utf8 = Charset.forName("UTF-8");
            SocketChannel sc = null;
            Selector selector = null;
            Random random = new Random();

            try {
                sc = SocketChannel.open();

                sc.configureBlocking(false);

                selector = Selector.open();

                int interestKey = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
                sc.register(selector, interestKey, new Buffers(256, 256));

                sc.connect(remoteAddress);

                //等待TCP连接完成
                while (!sc.finishConnect()) {
                    ;
                }
                log.info(name + " " + "finished connection.");
            }catch (Exception e) {
                log.info("client connection failed.");
                return;
            }

            try {

                int i = 0;

                while (!Thread.currentThread().isInterrupted()){

                    //轮询事件阻塞
                    selector.select();

                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> it = selectionKeys.iterator();

                    while(it.hasNext()){

                        SelectionKey key = it.next();

                        it.remove();

                        Buffers buffers = (Buffers)key.attachment();
                        ByteBuffer readBuffer = buffers.getReadBuffer();
                        ByteBuffer writeBuffer = buffers.getWriteBuffer();

                        SocketChannel socketChannel = (SocketChannel) key.channel();

                        if(key.isReadable()){
                            socketChannel.read(readBuffer);
                            readBuffer.flip();

                            CharBuffer charBuffer = utf8.decode(readBuffer);
                            log.info("接受到服务端响应信息："+new String(charBuffer.array()));
                            readBuffer.clear();
                        }
                        if(key.isWritable()){
                            log.info("发送的信息："+name + i);
                            writeBuffer.put((name + i).getBytes("UTF-8"));
                            writeBuffer.flip();

                            socketChannel.write(writeBuffer);
                            writeBuffer.clear();
                            i++;
                        }
                        //Thread.sleep(1000 + random.nextInt(1000));
                        Thread.sleep(1000 + random.nextInt(1000));
                    }
                }

            }catch (Exception e){

            }finally {

            }

        }
    }

    public static void main(String[] args) throws InterruptedException {
        InetSocketAddress remoteAddress = new InetSocketAddress("192.168.3.14", 8080);

        Thread ta = new Thread(new TCPEchoClient("thread a", remoteAddress));
        //Thread tb = new Thread(new TCPEchoClient("thread b", remoteAddress));
        //Thread tc = new Thread(new TCPEchoClient("thread c", remoteAddress));
        //Thread td = new Thread(new TCPEchoClient("thread d", remoteAddress));

        ta.start();
        //tb.start();
        //tc.start();

        Thread.sleep(5000);

        /*结束客户端a*/
        //ta.interrupt();

        /*开始客户端d*/
        //td.start();

    }

}
