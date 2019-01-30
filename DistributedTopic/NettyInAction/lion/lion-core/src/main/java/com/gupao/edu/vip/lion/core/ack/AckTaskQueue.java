
package com.gupao.edu.vip.lion.core.ack;

import com.gupao.edu.vip.lion.api.service.BaseService;
import com.gupao.edu.vip.lion.api.service.Listener;
import com.gupao.edu.vip.lion.core.LionServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public final class AckTaskQueue extends BaseService {
    private static final int DEFAULT_TIMEOUT = 3000;

    private final Logger logger = LoggerFactory.getLogger(AckTaskQueue.class);

    private final ConcurrentMap<Integer, AckTask> queue = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduledExecutor;
    private LionServer lionServer;

    public AckTaskQueue(LionServer lionServer) {
        this.lionServer = lionServer;
    }

    public void add(AckTask task, int timeout) {
        queue.put(task.getAckMessageId(), task);
        task.setAckTaskQueue(this);
        task.setFuture(scheduledExecutor.schedule(task,//使用 task.getExecutor() 并没更快
                timeout > 0 ? timeout : DEFAULT_TIMEOUT,
                TimeUnit.MILLISECONDS
        ));

        logger.debug("one ack task add to queue, task={}, timeout={}", task, timeout);
    }

    public AckTask getAndRemove(int sessionId) {
        return queue.remove(sessionId);
    }

    @Override
    protected void doStart(Listener listener) throws Throwable {
        scheduledExecutor = lionServer.getMonitor().getThreadPoolManager().getAckTimer();
        super.doStart(listener);
    }

    @Override
    protected void doStop(Listener listener) throws Throwable {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdown();
        }
        super.doStop(listener);
    }
}
