
package com.gupao.edu.vip.lion.test.sever;

import com.gupao.edu.vip.lion.bootstrap.Main;
import org.junit.Test;

import java.util.concurrent.locks.LockSupport;

/**
 */
public class ServerTestMain {

    public static void main(String[] args) {
        start();
    }

    @Test
    public void testServer() {
        start();
        LockSupport.park();
    }

    public static void start() {
        System.setProperty("io.netty.leakDetection.level", "PARANOID");
        System.setProperty("io.netty.noKeySetOptimization", "false");
        Main.main(null);
    }

}
