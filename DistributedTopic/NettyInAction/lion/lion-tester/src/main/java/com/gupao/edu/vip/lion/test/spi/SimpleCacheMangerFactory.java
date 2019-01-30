
package com.gupao.edu.vip.lion.test.spi;

import com.gupao.edu.vip.lion.api.spi.Spi;
import com.gupao.edu.vip.lion.api.spi.common.CacheManager;
import com.gupao.edu.vip.lion.api.spi.common.CacheManagerFactory;

/**
 */
@Spi(order = 2)
public final class SimpleCacheMangerFactory implements CacheManagerFactory {
    @Override
    public CacheManager get() {
        return FileCacheManger.I;
    }
}
