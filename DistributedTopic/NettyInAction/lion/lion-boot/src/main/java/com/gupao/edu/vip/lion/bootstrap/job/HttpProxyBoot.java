
package com.gupao.edu.vip.lion.bootstrap.job;

import com.gupao.edu.vip.lion.api.spi.net.DnsMappingManager;
import com.gupao.edu.vip.lion.core.LionServer;

/**
 */
public final class HttpProxyBoot extends BootJob {

    private final LionServer lionServer;

    public HttpProxyBoot(LionServer lionServer) {
        this.lionServer = lionServer;
    }

    @Override
    protected void start() {
        lionServer.getHttpClient().syncStart();
        DnsMappingManager.create().start();

        startNext();
    }

    @Override
    protected void stop() {
        stopNext();
        lionServer.getHttpClient().syncStop();
        DnsMappingManager.create().stop();
    }
}
