
package com.gupao.edu.vip.lion.bootstrap.job;

import com.gupao.edu.vip.lion.api.spi.common.CacheManagerFactory;

/**
 */
public final class CacheManagerBoot extends BootJob {

    @Override
    protected void start() {
        CacheManagerFactory.create().init();
        startNext();
    }

    @Override
    protected void stop() {
        stopNext();
        CacheManagerFactory.create().destroy();
    }
}
