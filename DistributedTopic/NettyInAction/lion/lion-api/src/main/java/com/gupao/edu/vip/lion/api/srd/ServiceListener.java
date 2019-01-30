
package com.gupao.edu.vip.lion.api.srd;

/**
 *
 *
 */
public interface ServiceListener {

    void onServiceAdded(String path, ServiceNode node);

    void onServiceUpdated(String path, ServiceNode node);

    void onServiceRemoved(String path, ServiceNode node);

}
