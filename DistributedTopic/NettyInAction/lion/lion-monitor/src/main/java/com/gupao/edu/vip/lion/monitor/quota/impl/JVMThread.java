
package com.gupao.edu.vip.lion.monitor.quota.impl;

import com.google.common.collect.Maps;
import com.gupao.edu.vip.lion.monitor.quota.ThreadQuota;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Map;

public class JVMThread implements ThreadQuota {

    private ThreadMXBean threadMXBean;

    public JVMThread() {
        threadMXBean = ManagementFactory.getThreadMXBean();
    }

    @Override
    public int daemonThreadCount() {
        return threadMXBean.getDaemonThreadCount();
    }

    @Override
    public int threadCount() {
        return threadMXBean.getThreadCount();
    }

    @Override
    public long totalStartedThreadCount() {
        return threadMXBean.getTotalStartedThreadCount();
    }

    @Override
    public int deadLockedThreadCount() {
        try {
            long[] deadLockedThreadIds = threadMXBean.findDeadlockedThreads();
            if (deadLockedThreadIds == null) {
                return 0;
            }
            return deadLockedThreadIds.length;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Object monitor(Object... args) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("daemonThreadCount", daemonThreadCount());
        map.put("threadCount", threadCount());
        map.put("totalStartedThreadCount", totalStartedThreadCount());
        map.put("deadLockedThreadCount", deadLockedThreadCount());
        return map;
    }
}
