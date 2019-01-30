
package com.gupao.edu.vip.lion.common.qps;

import java.util.concurrent.TimeUnit;

/**
 *
 *
 *
 */
public final class FastFlowControl implements FlowControl {
    private final int limit;
    private final int maxLimit;
    private final long duration;
    private final long start0 = System.nanoTime();
    private int count;
    private int total;
    private long start;


    public FastFlowControl(int limit, int maxLimit, int duration) {
        this.limit = limit;
        this.maxLimit = maxLimit;
        this.duration = TimeUnit.MILLISECONDS.toNanos(duration);
    }

    public FastFlowControl(int qps) {
        this(qps, Integer.MAX_VALUE, 1000);
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
    public boolean checkQps() {
        if (count < limit) {
            count++;
            total++;
            return true;
        }

        if (total > maxLimit) throw new OverFlowException(true);

        if (System.nanoTime() - start > duration) {
            reset();
            total++;
            return true;
        }
        return false;
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
}
