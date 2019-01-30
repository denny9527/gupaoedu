
package com.gupao.edu.vip.lion.test.udp;

import com.gupao.edu.vip.lion.tools.Utils;
import org.junit.Test;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 */
public final class MulticastTest {
    @Test
    public void TestServer() throws Exception {
        //接受组播和发送组播的数据报服务都要把组播地址添加进来
        String host = "239.239.239.88";//多播地址
        int port = 9998;
        InetAddress group = InetAddress.getByName(host);

        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);
        channel.bind(new InetSocketAddress(port));
        channel.join(group, Utils.getLocalNetworkInterface());
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketAddress sender = channel.receive(buffer);
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        System.out.println(new String(data));

    }

    @Test
    public void testSend() throws Exception {
        String host = "239.239.239.99";//多播地址
        int port = 9999;
        InetAddress group = InetAddress.getByName(host);
        String message = "test-multicastSocket";

        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);
        channel.configureBlocking(true);
        channel.bind(new InetSocketAddress(port));
        channel.join(group, Utils.getLocalNetworkInterface());

        InetSocketAddress sender = new InetSocketAddress("239.239.239.99", 4000);
        channel.send(ByteBuffer.wrap(message.getBytes()), sender);

        channel.close();
    }
}
