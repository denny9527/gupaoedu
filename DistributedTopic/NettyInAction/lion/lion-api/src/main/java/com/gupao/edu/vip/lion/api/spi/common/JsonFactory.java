
package com.gupao.edu.vip.lion.api.spi.common;

import com.gupao.edu.vip.lion.api.spi.Factory;
import com.gupao.edu.vip.lion.api.spi.SpiLoader;


public interface JsonFactory extends Factory<Json> {

    static Json create() {
        return SpiLoader.load(JsonFactory.class).get();
    }
}
