package com.denny.nio.channel;

import com.denny.nio.buffer.Buffers;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

@Slf4j
public class ServerSocketChannelDemo {

    public static class TCPEchoServer implements  Runnable {

        private InetSocketAddress localAddress;

        public TCPEchoServer(int port) {
            this.localAddress = new InetSocketAddress(port);
        }

        @Override
        public void run() {

            Charset utf8 = Charset.forName("UTF-8");

            ServerSocketChannel ssc = null;
            Selector selector = null;

            Random random = null;

            //初始化服务器
            try {

                random = new Random();
                /*创建选择器*/
                selector = Selector.open();

                /*创建服务器通道*/
                ssc = ServerSocketChannel.open();
                /*设置通道为非阻塞*/
                ssc.configureBlocking(false);

                /*设置监听服务器的端口，设置最大连接缓存数为100*/
                ssc.bind(localAddress, 100);//客户端连接

                /*服务器通道只能对tcp连接事件感兴趣*/
                ssc.register(selector, SelectionKey.OP_ACCEPT);

            }catch (Exception e){
                log.info(e.getLocalizedMessage());
            }

            System.out.println("server start with address：" + localAddress.getHostString() + ":" + localAddress.getPort());

            //监听事件
            try {

                while(!Thread.currentThread().isInterrupted()){
                    //当注册事件(至少一个channel被选择) 到达时，方法返回，否则将一直阻塞
                    int n = selector.select();
                    if(n == 0){
                        continue;
                    }

                    Set<SelectionKey> keySet = selector.selectedKeys();//key 对应事件
                    Iterator<SelectionKey> it = keySet.iterator();
                    SelectionKey key = null;

                    try {
                        while (it.hasNext()) {
                            key = it.next();

                            /*处理的事件删除掉*/
                            it.remove();//需要人工清除下次轮询避免重复处理

                            if (key.isAcceptable()) {
                                SocketChannel socketChannel = ssc.accept();
                                socketChannel.configureBlocking(false);

                                int interestSet = SelectionKey.OP_READ;
                                socketChannel.register(selector, interestSet, new Buffers(256, 256));

                                log.info("accept form " + socketChannel.getRemoteAddress());
                            }

                            if (key.isReadable()) {
                                Buffers buffers = (Buffers) key.attachment();
                                ByteBuffer readBuffer = buffers.getReadBuffer();
                                ByteBuffer writerBuffer = buffers.getWriteBuffer();

                                SocketChannel sc = (SocketChannel) key.channel();
                                sc.read(readBuffer);

                                readBuffer.flip();

                                CharBuffer charBuffer = utf8.decode(readBuffer);
                                log.info("接受到的信息内容：" + new String(charBuffer.array()));

                                readBuffer.rewind();//再次读取

                                log.info(writerBuffer.position() + "");
                                writerBuffer.put("echo from service:".getBytes("UTF-8"));
                                writerBuffer.put(readBuffer);

                                readBuffer.clear();

                                //writerBuffer.flip();
                                //sc.write(writerBuffer); //无需注册Write事件

                               // writerBuffer.clear();

                                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);

                            }

                           if (key.isWritable()) {
                                Buffers buffers = (Buffers) key.attachment();
                                ByteBuffer writerBuffer = buffers.getWriteBuffer();
                                writerBuffer.flip();

                                SocketChannel socketChannel = (SocketChannel) key.channel();

                                socketChannel.write(writerBuffer);

                                int len = 0;

                                while (writerBuffer.hasRemaining()) {
                                    len = socketChannel.write(writerBuffer);

                                    if (len == 0) {
                                        break;
                                    }
                                }

                                writerBuffer.compact();//读取部分，继续写入
                                System.out.println("len:"+len);
                                //if (len != 0) {
                                    key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
                                //}

                            }
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getLocalizedMessage());
                        key.cancel();

                        key.channel().close();
                    }
                    Thread.sleep(random.nextInt(500));
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getLocalizedMessage());
            } finally {
                try {
                    ssc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new TCPEchoServer(8080));
        thread.start();
        Thread.sleep(100000);
        thread.interrupt();
    }

}
