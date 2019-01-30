package com.denny.nio.channel;

import com.denny.nio.buffer.Buffers;

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


/*服务器端，:接收客户端发送过来的数据并显示，
 *服务器把上接收到的数据加上"echo from service:"再发送回去*/
public class ServiceSocketChannelDemo {

    public static class TCPEchoServer implements Runnable{

        /*服务器地址*/
        private InetSocketAddress localAddress;

        public TCPEchoServer(int port) throws IOException{
            this.localAddress = new InetSocketAddress(port);
        }


        @Override
        public void run(){

            Charset utf8 = Charset.forName("UTF-8");

            ServerSocketChannel ssc = null;
            Selector selector = null;

            Random rnd = new Random();

            try {
                /*创建选择器*/
                selector = Selector.open();

                /*创建服务器通道*/
                ssc = ServerSocketChannel.open();
                ssc.configureBlocking(false);

                /*设置监听服务器的端口，设置最大连接缓冲数为100*/
                ssc.bind(localAddress, 100);

                /*服务器通道只能对tcp链接事件感兴趣*/
                ssc.register(selector, SelectionKey.OP_ACCEPT);

            } catch (IOException e1) {
                System.out.println("server start failed");
                return;
            }

            System.out.println("server start with address : " + localAddress);

            /*服务器线程被中断后会退出*/
            try{

                while(!Thread.currentThread().isInterrupted()){

                    int n = selector.select();
                    if(n == 0){
                        continue;
                    }

                    Set<SelectionKey> keySet = selector.selectedKeys();
                    Iterator<SelectionKey> it = keySet.iterator();
                    SelectionKey key = null;

                    while(it.hasNext()){

                        key = it.next();
                        /*防止下次select方法返回已处理过的通道*/
                        it.remove();

                        /*若发现异常，说明客户端连接出现问题,但服务器要保持正常*/
                        try{
                            /*ssc通道只能对链接事件感兴趣*/
                            if(key.isAcceptable()){

                                /*accept方法会返回一个普通通道，
                                     每个通道在内核中都对应一个socket缓冲区*/
                                SocketChannel sc = ssc.accept();
                                sc.configureBlocking(false);

                                /*向选择器注册这个通道和普通通道感兴趣的事件，同时提供这个新通道相关的缓冲区*/
                                int interestSet = SelectionKey.OP_READ;
                                sc.register(selector, interestSet, new Buffers(256, 256));

                                System.out.println("accept from " + sc.getRemoteAddress());
                            }


                            /*（普通）通道感兴趣读事件且有数据可读*/
                            if(key.isReadable()){

                                /*通过SelectionKey获取通道对应的缓冲区*/
                                Buffers  buffers = (Buffers)key.attachment();
                                ByteBuffer readBuffer = buffers.getReadBuffer();
                                ByteBuffer writeBuffer = buffers.getWriteBuffer();

                                /*通过SelectionKey获取对应的通道*/
                                SocketChannel sc = (SocketChannel) key.channel();

                                /*从底层socket读缓冲区中读入数据*/
                                sc.read(readBuffer);
                                readBuffer.flip();

                                /*解码显示，客户端发送来的信息*/
                                CharBuffer cb = utf8.decode(readBuffer);
                                System.out.println(cb.array());

                                readBuffer.rewind();


                                /*准备好向客户端发送的信息*/
                                /*先写入"echo:"，再写入收到的信息*/
                                writeBuffer.put("echo from service:".getBytes("UTF-8"));
                                writeBuffer.put(readBuffer);

                                readBuffer.clear();

                                /*设置通道写事件*/
                                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);

                            }

                            /*通道感兴趣写事件且底层缓冲区有空闲*/
                            if(key.isWritable()){

                                Buffers  buffers = (Buffers)key.attachment();

                                ByteBuffer writeBuffer = buffers.getWriteBuffer();
                                writeBuffer.flip();

                                SocketChannel sc = (SocketChannel) key.channel();

                                int len = 0;
                                while(writeBuffer.hasRemaining()){
                                    len = sc.write(writeBuffer);
                                    /*说明底层的socket写缓冲已满*/
                                    if(len == 0){
                                        break;
                                    }
                                }

                                writeBuffer.compact();

                                /*说明数据全部写入到底层的socket写缓冲区*/
                                if(len != 0){
                                    /*取消通道的写事件*/
                                    key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
                                }

                            }
                        }catch(IOException e){
                            System.out.println("service encounter client error");
                            /*若客户端连接出现异常，从Seletcor中移除这个key*/
                            key.cancel();
                            key.channel().close();
                        }

                    }

                    Thread.sleep(rnd.nextInt(500));
                }

            }catch(InterruptedException e){
                System.out.println("serverThread is interrupted");
            } catch (IOException e1) {
                System.out.println("serverThread selecotr error");
            }finally{
                try{
                    selector.close();
                }catch(IOException e){
                    System.out.println("selector close failed");
                }finally{
                    System.out.println("server close");
                }
            }

        }
    }

    public static void main(String[] args) throws InterruptedException, IOException{
        Thread thread = new Thread(new TCPEchoServer(8080));
        thread.start();
        Thread.sleep(100000);
        /*结束服务器线程*/
        thread.interrupt();
    }

}