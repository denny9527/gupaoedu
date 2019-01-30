
package com.gupao.edu.vip.lion.api.spi.handler;

import com.gupao.edu.vip.lion.api.spi.Plugin;


public interface BindValidator extends Plugin {
    boolean validate(String userId, String data);
}
