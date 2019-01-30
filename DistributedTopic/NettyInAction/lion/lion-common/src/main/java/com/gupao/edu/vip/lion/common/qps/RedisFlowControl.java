
package com.gupao.edu.vip.lion.common.qps;

import com.gupao.edu.vip.lion.api.push.BroadcastController;
import com.gupao.edu.vip.lion.common.push.RedisBroadcastController;

import java.util.concurrent.TimeUnit;

/**
 *
 *
 */
public final class RedisFlowControl implements FlowControl {

    private final BroadcastController controller;
    private final long start0 = System.nanoTime();
    private final long duration = TimeUnit.SECONDS.toNanos(1);
    private final int maxLimit;
    private int limit;
    private int count;
    private int total;
    private long start;

    public RedisFlowControl(String taskId) {
        this(taskId, Integer.MAX_VALUE);
    }

    public RedisFlowControl(String taskId, int maxLimit) {
        this.controller = new RedisBroadcastController(taskId);
        this.limit = controller.qps();
        this.maxLimit = maxLimit;
    }

    @Override
    public void reset() {
        count = 0;
        start = System.nanoTime();
    }

    @Override
    public int total() {
        return total;
    }

    @Override
    public boolean checkQps() throws OverFlowException {
        if (count < limit) {
            count++;
            total++;
            return true;
        }

        if (total() > maxLimit) {
            throw new OverFlowException(true);
        }

        if (System.nanoTime() - start > duration) {
            reset();
            total++;
            return true;
        }

        if (controller.isCancelled()) {
            throw new OverFlowException(true);
        } else {
            limit = controller.qps();
        }
        return false;
    }

    @Override
    public void end(Object result) {
        int t = total;
        if (total > 0) {
            total = 0;
            controller.incSendCount(t);
        }

        if (result != null && (result instanceof String[])) {
            controller.success((String[]) result);
        }
    }

    @Override
    public long getDelay() {
        return duration - (System.nanoTime() - start);
    }

    @Override
    public String report() {
        return String.format("total:%d, count:%d, qps:%d", total, count, qps());
    }

    @Override
    public int qps() {
        return (int) (TimeUnit.SECONDS.toNanos(total) / (System.nanoTime() - start0));
    }

    public BroadcastController getController() {
        return controller;
    }
}
