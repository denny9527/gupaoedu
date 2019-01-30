
package com.gupao.edu.vip.lion.test.client;

import com.gupao.edu.vip.lion.api.srd.ServiceNode;
import com.gupao.edu.vip.lion.client.connect.ClientConfig;
import com.gupao.edu.vip.lion.client.connect.ConnClientChannelHandler;
import com.gupao.edu.vip.lion.common.security.CipherBox;
import com.gupao.edu.vip.lion.tools.log.Logs;
import io.netty.channel.ChannelFuture;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ConnClientTestMain {

    public static void main(String[] args) throws Exception {
        int count = 1;
        String userPrefix = "";
        int printDelay = 1;
        boolean sync = true;

        if (args.length > 0) {
            count = NumberUtils.toInt(args[0], count);
        }

        if (args.length > 1) {
            userPrefix = args[1];
        }

        if (args.length > 2) {
            printDelay = NumberUtils.toInt(args[2], printDelay);
        }

        if (args.length > 3) {
            sync = !"1".equals(args[3]);
        }

        testConnClient(count, userPrefix, printDelay, sync);
    }

    @Test
    public void testConnClient() throws Exception {
        testConnClient(1, "", 1, true);
        LockSupport.park();
    }

    private static void testConnClient(int count, String userPrefix, int printDelay, boolean sync) throws Exception {
        Logs.init();
        ConnClientBoot boot = new ConnClientBoot();
        boot.start().get();

        List<ServiceNode> serverList = boot.getServers();
        if (serverList.isEmpty()) {
            boot.stop();
            System.out.println("no lion server.");
            return;
        }

        if (printDelay > 0) {
            Executors.newSingleThreadScheduledExecutor()
                    .scheduleAtFixedRate(
                            () -> System.err.println(ConnClientChannelHandler.STATISTICS)
                            , 3, printDelay, TimeUnit.SECONDS
                    );
        }

        for (int i = 0; i < count; i++) {
            String clientVersion = "1.0." + i;
            String osName = "android";
            String osVersion = "1.0.1";
            String userId = userPrefix + "user-" + i;
            String deviceId = userPrefix + "test-device-id-" + i;
            byte[] clientKey = CipherBox.I.randomAESKey();
            byte[] iv = CipherBox.I.randomAESIV();

            ClientConfig config = new ClientConfig();
            config.setClientKey(clientKey);
            config.setIv(iv);
            config.setClientVersion(clientVersion);
            config.setDeviceId(deviceId);
            config.setOsName(osName);
            config.setOsVersion(osVersion);
            config.setUserId(userId);

            int L = serverList.size();
            int index = (int) ((Math.random() % L) * L);
            ServiceNode node = serverList.get(index);

            ChannelFuture future = boot.connect(node.getHost(), node.getPort(), config);
            if (sync) future.awaitUninterruptibly();
        }
    }
}
