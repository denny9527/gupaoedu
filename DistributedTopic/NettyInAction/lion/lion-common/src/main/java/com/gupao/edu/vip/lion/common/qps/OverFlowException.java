
package com.gupao.edu.vip.lion.common.qps;

/**
 *
 *
 *
 */
public final class OverFlowException extends RuntimeException {

    private boolean overMaxLimit = false;

    public OverFlowException() {
        super(null, null, false, false);
    }

    public OverFlowException(boolean overMaxLimit) {
        super(null, null, false, false);
        this.overMaxLimit = overMaxLimit;
    }

    public OverFlowException(String message) {
        super(message, null, false, false);
    }

    public boolean isOverMaxLimit() {
        return overMaxLimit;
    }
}
