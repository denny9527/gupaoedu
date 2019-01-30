
package com.gupao.edu.vip.lion.api.spi.common;


public interface MQMessageReceiver {
    void receive(String topic, Object message);
}
