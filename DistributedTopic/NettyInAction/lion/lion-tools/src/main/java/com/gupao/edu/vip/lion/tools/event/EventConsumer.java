
package com.gupao.edu.vip.lion.tools.event;

public abstract class EventConsumer {

    public EventConsumer() {
        EventBus.register(this);
    }

}
