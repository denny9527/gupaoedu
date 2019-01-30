
package com.gupao.edu.vip.lion.bootstrap.job;

import com.gupao.edu.vip.lion.core.LionServer;

/**
 *
 */
public final class PushCenterBoot extends BootJob {
    private final LionServer lionServer;

    public PushCenterBoot(LionServer lionServer) {
        this.lionServer = lionServer;
    }

    @Override
    protected void start() {
        lionServer.getPushCenter().start();
        startNext();
    }

    @Override
    protected void stop() {
        stopNext();
        lionServer.getPushCenter().stop();
    }
}
