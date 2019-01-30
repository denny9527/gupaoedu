
package com.gupao.edu.vip.lion.client.push;

import com.gupao.edu.vip.lion.api.service.BaseService;
import com.gupao.edu.vip.lion.api.service.Listener;
import com.gupao.edu.vip.lion.client.LionClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 */
public class PushRequestBus extends BaseService {
    private final Logger logger = LoggerFactory.getLogger(PushRequestBus.class);
    private final Map<Integer, PushRequest> reqQueue = new ConcurrentHashMap<>(1024);
    private ScheduledExecutorService scheduledExecutor;
    private final LionClient lionClient;

    public PushRequestBus(LionClient lionClient) {
        this.lionClient = lionClient;
    }

    public Future<?> put(int sessionId, PushRequest request) {
        reqQueue.put(sessionId, request);
        return scheduledExecutor.schedule(request, request.getTimeout(), TimeUnit.MILLISECONDS);
    }

    public PushRequest getAndRemove(int sessionId) {
        return reqQueue.remove(sessionId);
    }

    public void asyncCall(Runnable runnable) {
        scheduledExecutor.execute(runnable);
    }

    @Override
    protected void doStart(Listener listener) throws Throwable {
        scheduledExecutor = lionClient.getThreadPoolManager().getPushClientTimer();
        listener.onSuccess();
    }

    @Override
    protected void doStop(Listener listener) throws Throwable {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdown();
        }
        listener.onSuccess();
    }
}
