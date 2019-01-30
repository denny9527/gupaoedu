
package com.gupao.edu.vip.lion.core.mq;

import java.util.Collection;
import java.util.Collections;


public final class MQClient {

    public void init() {

    }

    public void subscribe(String topic, MQMessageReceiver listener) {

    }

    public void publish(String topic, MQPushMessage message) {

    }

    public Collection<MQPushMessage> take(String topic) {
        return Collections.emptyList();
    }
}
