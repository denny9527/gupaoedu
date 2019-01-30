
package com.gupao.edu.vip.lion.client.push;

import com.gupao.edu.vip.lion.api.push.PushSender;
import com.gupao.edu.vip.lion.api.spi.Spi;
import com.gupao.edu.vip.lion.api.spi.client.PusherFactory;

/**
 */
@Spi
public class PushClientFactory implements PusherFactory {
    private volatile PushClient client;

    @Override
    public PushSender get() {
        if (client == null) {
            synchronized (PushClientFactory.class) {
                if (client == null) {
                    client = new PushClient();
                }
            }
        }
        return client;
    }
}
