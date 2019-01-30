
package com.gupao.edu.vip.lion.api.srd;

import com.gupao.edu.vip.lion.api.service.Service;

/**
 *
 *
 */
public interface ServiceRegistry extends Service {

    void register(ServiceNode node);

    void deregister(ServiceNode node);
}
