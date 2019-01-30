
package com.gupao.edu.vip.lion.api.spi.net;

import com.gupao.edu.vip.lion.api.service.Service;
import com.gupao.edu.vip.lion.api.spi.SpiLoader;

/**
 *
 */
public interface DnsMappingManager extends Service {

    static DnsMappingManager create() {
        return SpiLoader.load(DnsMappingManager.class);
    }

    DnsMapping lookup(String origin);
}
