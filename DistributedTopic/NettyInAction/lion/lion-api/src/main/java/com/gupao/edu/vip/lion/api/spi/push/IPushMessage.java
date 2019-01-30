

package com.gupao.edu.vip.lion.api.spi.push;

import com.gupao.edu.vip.lion.api.common.Condition;

/**
 *
 *
 */
public interface IPushMessage {

    boolean isBroadcast();

    String getUserId();

    int getClientType();

    byte[] getContent();

    boolean isNeedAck();

    byte getFlags();

    int getTimeoutMills();

    default String getTaskId() {
        return null;
    }

    default Condition getCondition() {
        return null;
    }

    default void finalized() {

    }

}
