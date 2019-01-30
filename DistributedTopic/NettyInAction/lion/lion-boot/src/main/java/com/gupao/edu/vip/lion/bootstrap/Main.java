
package com.gupao.edu.vip.lion.bootstrap;

import com.gupao.edu.vip.lion.tools.log.Logs;

public class Main {

    /**
     *
     */
    public static void main(String[] args) {
        Logs.init();
        Logs.Console.info("launch Lion server...");
        ServerLauncher launcher = new ServerLauncher();
        launcher.init();
        launcher.start();
        addHook(launcher);
    }

    /**
     * 注意点
     * 1.不要ShutdownHook Thread 里调用System.exit()方法，否则会造成死循环。
     * 2.如果有非守护线程，只有所有的非守护线程都结束了才会执行hook
     * 3.Thread默认都是非守护线程，创建的时候要注意
     * 4.注意线程抛出的异常，如果没有被捕获都会跑到Thread.dispatchUncaughtException
     *
     * @param launcher
     */
    private static void addHook(ServerLauncher launcher) {
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {

                    try {
                        launcher.stop();
                    } catch (Exception e) {
                        Logs.Console.error("Lion server stop ex", e);
                    }
                    Logs.Console.info("jvm exit, all service stopped.");

                }, "lion-shutdown-hook-thread")
        );
    }
}
