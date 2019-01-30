
package com.gupao.edu.vip.lion.monitor.service;

import com.gupao.edu.vip.lion.api.spi.common.ExecutorFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

public final class ThreadPoolManager {

    private final ExecutorFactory executorFactory = ExecutorFactory.create();

    private final Map<String, Executor> pools = new ConcurrentHashMap<>();

    public Executor getRedisExecutor() {
        return pools.computeIfAbsent("mq", s -> executorFactory.get(ExecutorFactory.MQ));
    }

    public Executor getEventBusExecutor() {
        return pools.computeIfAbsent("event-bus", s -> executorFactory.get(ExecutorFactory.EVENT_BUS));
    }

    public ScheduledExecutorService getPushClientTimer() {
        return (ScheduledExecutorService) pools.computeIfAbsent("push-client-timer"
                , s -> executorFactory.get(ExecutorFactory.PUSH_CLIENT));
    }

    public ScheduledExecutorService getPushTaskTimer() {
        return (ScheduledExecutorService) pools.computeIfAbsent("push-task-timer"
                , s -> executorFactory.get(ExecutorFactory.PUSH_TASK));
    }

    public ScheduledExecutorService getAckTimer() {
        return (ScheduledExecutorService) pools.computeIfAbsent("ack-timer"
                , s -> executorFactory.get(ExecutorFactory.ACK_TIMER));
    }

    public void register(String name, Executor executor) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(executor);
        pools.put(name, executor);
    }

    public Map<String, Executor> getActivePools() {
        return pools;
    }

}
