
package com.gupao.edu.vip.lion.monitor.quota;


public interface ThreadQuota extends MonitorQuota {

    int daemonThreadCount();

    int threadCount();

    long totalStartedThreadCount();

    int deadLockedThreadCount();

}
