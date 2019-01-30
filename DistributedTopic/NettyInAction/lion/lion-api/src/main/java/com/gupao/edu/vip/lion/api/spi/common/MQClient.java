
package com.gupao.edu.vip.lion.api.spi.common;

import com.gupao.edu.vip.lion.api.spi.Plugin;


public interface MQClient extends Plugin {

    void subscribe(String topic, MQMessageReceiver receiver);

    void publish(String topic, Object message);
}
