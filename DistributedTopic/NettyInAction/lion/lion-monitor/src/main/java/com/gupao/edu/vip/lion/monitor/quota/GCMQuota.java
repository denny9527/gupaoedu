
package com.gupao.edu.vip.lion.monitor.quota;

public interface GCMQuota extends MonitorQuota {

    long yongGcCollectionCount();

    long yongGcCollectionTime();

    long fullGcCollectionCount();

    long fullGcCollectionTime();

    long spanYongGcCollectionCount();

    long spanYongGcCollectionTime();

    long spanFullGcCollectionCount();

    long spanFullGcCollectionTime();

}
