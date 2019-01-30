
package com.gupao.edu.vip.lion.bootstrap.job;

import com.gupao.edu.vip.lion.core.LionServer;

/**
 */
public final class MonitorBoot extends BootJob {

    private final LionServer lionServer;

    public MonitorBoot(LionServer lionServer) {
        this.lionServer = lionServer;
    }

    @Override
    protected void start() {
        lionServer.getMonitor().start();
        startNext();
    }

    @Override
    protected void stop() {
        stopNext();
        lionServer.getMonitor().stop();
    }
}
