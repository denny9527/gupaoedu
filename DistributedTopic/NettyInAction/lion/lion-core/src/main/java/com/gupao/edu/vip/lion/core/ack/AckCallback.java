
package com.gupao.edu.vip.lion.core.ack;


public interface AckCallback {
    void onSuccess(AckTask context);

    void onTimeout(AckTask context);
}
