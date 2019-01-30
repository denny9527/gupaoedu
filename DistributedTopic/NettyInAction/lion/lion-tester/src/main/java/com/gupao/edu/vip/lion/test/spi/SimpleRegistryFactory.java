
package com.gupao.edu.vip.lion.test.spi;

import com.gupao.edu.vip.lion.api.spi.Spi;
import com.gupao.edu.vip.lion.api.spi.common.ServiceRegistryFactory;
import com.gupao.edu.vip.lion.api.srd.ServiceRegistry;

/**
 */
@Spi(order = 2)
public final class SimpleRegistryFactory implements ServiceRegistryFactory {
    @Override
    public ServiceRegistry get() {
        return FileSrd.I;
    }
}
