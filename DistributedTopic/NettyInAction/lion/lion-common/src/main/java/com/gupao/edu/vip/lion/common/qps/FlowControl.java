
package com.gupao.edu.vip.lion.common.qps;

/**
 *
 *
 *
 */
public interface FlowControl {

    void reset();

    int total();

    /**
     * 判断瞬时qps是否超出设定的流量
     *
     * @return true/false
     * @throws OverFlowException 超出最大限制，会直接抛出异常
     */
    boolean checkQps() throws OverFlowException;

    default void end(Object results) {
    }

    /**
     * 超出流控的任务，应该延迟执行的时间(ns)
     *
     * @return 单位纳秒
     */
    long getDelay();

    /**
     * 任务从开始到现在的平均qps
     *
     * @return 平均qps
     */
    int qps();

    String report();

}
