
package com.gupao.edu.vip.lion.test.spi;

import com.gupao.edu.vip.lion.api.spi.Spi;
import com.gupao.edu.vip.lion.api.spi.common.MQClient;
import com.gupao.edu.vip.lion.api.spi.common.MQMessageReceiver;

/**
 */
@Spi(order = 2)
public final class SimpleMQClientFactory implements com.gupao.edu.vip.lion.api.spi.common.MQClientFactory {
    private MQClient mqClient = new MQClient() {
        @Override
        public void subscribe(String topic, MQMessageReceiver receiver) {
            System.err.println("subscribe " + topic);
        }

        @Override
        public void publish(String topic, Object message) {
            System.err.println("publish " + topic + " " + message);
        }
    };

    @Override
    public MQClient get() {
        return mqClient;
    }
}
