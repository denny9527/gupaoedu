
package com.gupao.edu.vip.lion.bootstrap.job;

import com.gupao.edu.vip.lion.api.spi.common.ServiceRegistryFactory;
import com.gupao.edu.vip.lion.tools.log.Logs;

/**
 */
public final class ServiceRegistryBoot extends BootJob {

    @Override
    protected void start() {
        Logs.Console.info("init service registry waiting for connected...");
        ServiceRegistryFactory.create().syncStart();
        startNext();
    }

    @Override
    protected void stop() {
        stopNext();
        ServiceRegistryFactory.create().syncStop();
        Logs.Console.info("service registry closed...");
    }
}
