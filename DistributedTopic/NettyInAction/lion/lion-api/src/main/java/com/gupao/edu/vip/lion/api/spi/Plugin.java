
package com.gupao.edu.vip.lion.api.spi;

import com.gupao.edu.vip.lion.api.LionContext;

/**
 *
 *
 *
 */
public interface Plugin {

    default void init(LionContext context) {

    }

    default void destroy() {

    }
}
