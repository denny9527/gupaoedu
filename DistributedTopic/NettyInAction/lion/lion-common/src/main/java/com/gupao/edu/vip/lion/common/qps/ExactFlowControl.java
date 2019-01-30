
package com.gupao.edu.vip.lion.common.qps;

import com.gupao.edu.vip.lion.tools.common.RollingNumber;

import java.util.concurrent.TimeUnit;

import static com.gupao.edu.vip.lion.tools.common.RollingNumber.Event.SUCCESS;

/**
 *
 *
 */
public final class ExactFlowControl implements FlowControl {
    private static final long DELAY_100_MS = TimeUnit.MILLISECONDS.toNanos(1);
    private final RollingNumber rollingNumber;
    private final int qps_pre_10_mills;
    private final long start0 = System.nanoTime();

    public ExactFlowControl(int qps) {
        int timeInMilliseconds = 1000;// 1s
        int numberOfBuckets = 100;//把1s 分成 100份，10ms一份， 要计算处 每10ms内允许的最大数量qps_pre_10_mills

        int _10_mills = timeInMilliseconds / numberOfBuckets;//=10

        double real_qps_pre_10_mills = (qps / 1000f) * _10_mills;

        if (real_qps_pre_10_mills < 1) {//qps < 100;
            numberOfBuckets = 1;
            real_qps_pre_10_mills = qps;
        }

        this.qps_pre_10_mills = (int) real_qps_pre_10_mills;
        this.rollingNumber = new RollingNumber(timeInMilliseconds, numberOfBuckets);
    }

    @Override
    public void reset() {

    }

    @Override
    public int total() {
        return (int) rollingNumber.getCumulativeSum(SUCCESS);
    }

    @Override
    public boolean checkQps() throws OverFlowException {
        if (rollingNumber.getValueOfLatestBucket(SUCCESS) < qps_pre_10_mills) {
            rollingNumber.increment(SUCCESS);
            return true;
        }
        return false;
    }

    @Override
    public long getDelay() {
        return DELAY_100_MS;
    }

    @Override
    public int qps() {
        return (int) rollingNumber.getRollingSum(SUCCESS);
    }

    @Override
    public String report() {
        return String.format("total:%d, count:%d, qps:%d, avg_qps:%d",
                total(), rollingNumber.getValueOfLatestBucket(SUCCESS), qps(),
                TimeUnit.SECONDS.toNanos(total()) / (System.nanoTime() - start0));
    }
}
