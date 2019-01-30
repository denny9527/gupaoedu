
package com.gupao.edu.vip.lion.test.udp;

import com.gupao.edu.vip.lion.tools.Utils;
import org.junit.Test;

import java.io.IOException;
import java.net.*;

/**
 */
public final class MulticastTest2 {
    @Test
    public void TestServer() throws Exception {
        //接受组播和发送组播的数据报服务都要把组播地址添加进来
        String host = "239.239.239.99";//多播地址
        int port = 9998;
        int length = 1024;
        byte[] buf = new byte[length];
        MulticastSocket ms = null;
        DatagramPacket dp = null;
        StringBuffer sbuf = new StringBuffer();
        try {
            ms = new MulticastSocket(port);
            dp = new DatagramPacket(buf, length);

            //加入多播地址
            ms.joinGroup(new InetSocketAddress(host, port), Utils.getLocalNetworkInterface());
            System.out.println("监听多播端口打开：");
            ms.receive(dp);
            ms.close();
            int i;
            for (i = 0; i < 1024; i++) {
                if (buf[i] == 0) {
                    break;
                }
                sbuf.append((char) buf[i]);
            }
            System.out.println("收到多播消息：" + sbuf.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSend2() throws Exception {
        String host = "239.239.239.99";//多播地址
        int port = 9998;
        String message = "test-multicastSocket";
        //接受组播和发送组播的数据报服务都要把组播地址添加进来
        int length = 1024;
        byte[] buf = new byte[length];
        MulticastSocket ms = null;
        DatagramPacket dp = null;
        StringBuffer sbuf = new StringBuffer();
        try {
            ms = new MulticastSocket(port);
            dp = new DatagramPacket(buf, length);
            InetAddress group = InetAddress.getByName(host);
            //加入多播地址
            ms.joinGroup(new InetSocketAddress("239.239.239.88", 9999), Utils.getLocalNetworkInterface());
            System.out.println("监听多播端口打开：");
            DatagramPacket dp2 = new DatagramPacket(message.getBytes(), message.length(), group, port);
            ms.send(dp2);
            ms.receive(dp);
            ms.close();
            int i;
            for (i = 0; i < 1024; i++) {
                if (buf[i] == 0) {
                    break;
                }
                sbuf.append((char) buf[i]);
            }
            System.out.println("收到多播消息：" + sbuf.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSend() throws Exception {
        String host = "239.239.239.99";//多播地址
        int port = 9998;
        String message = "test-multicastSocket";
        try {
            InetAddress group = InetAddress.getByName(host);
            MulticastSocket s = new MulticastSocket();
            //加入多播组
            s.joinGroup(new InetSocketAddress(host, port), Utils.getLocalNetworkInterface());
            DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), group, port);
            s.send(dp);
            s.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
