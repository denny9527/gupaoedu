
package com.gupao.edu.vip.lion.bootstrap.job;

import com.gupao.edu.vip.lion.core.LionServer;

/**
 *
 */
public final class RouterCenterBoot extends BootJob {
    private final LionServer lionServer;

    public RouterCenterBoot(LionServer lionServer) {
        this.lionServer = lionServer;
    }

    @Override
    protected void start() {
        lionServer.getRouterCenter().start();
        startNext();
    }

    @Override
    protected void stop() {
        stopNext();
        lionServer.getRouterCenter().stop();
    }
}
