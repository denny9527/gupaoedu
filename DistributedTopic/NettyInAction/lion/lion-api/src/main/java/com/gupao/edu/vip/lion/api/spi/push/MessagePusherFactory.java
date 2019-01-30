
package com.gupao.edu.vip.lion.api.spi.push;

import com.gupao.edu.vip.lion.api.spi.Factory;
import com.gupao.edu.vip.lion.api.spi.SpiLoader;

/**
 *
 *
 */
public interface MessagePusherFactory extends Factory<MessagePusher> {

    static MessagePusher create() {
        return SpiLoader.load(MessagePusherFactory.class).get();
    }
}
