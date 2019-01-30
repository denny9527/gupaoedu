
package com.gupao.edu.vip.lion.common.push;

import com.gupao.edu.vip.lion.api.push.BroadcastController;
import com.gupao.edu.vip.lion.api.spi.common.CacheManager;
import com.gupao.edu.vip.lion.api.spi.common.CacheManagerFactory;
import com.gupao.edu.vip.lion.common.CacheKeys;

import java.util.List;

/**
 *
 *
 */
public final class RedisBroadcastController implements BroadcastController {
    private static final String TASK_DONE_FIELD = "d";
    private static final String TASK_SEND_COUNT_FIELD = "sc";
    private static final String TASK_CANCEL_FIELD = "c";
    private static final String TASK_QPS_FIELD = "q";
    private static final String TASK_SUCCESS_USER_ID = "sui";

    private static CacheManager cacheManager = CacheManagerFactory.create();

    private final String taskId;
    private final String taskKey;
    private final String taskSuccessUIDKey;

    public RedisBroadcastController(String taskId) {
        this.taskId = taskId;
        this.taskKey = CacheKeys.getPushTaskKey(taskId);
        this.taskSuccessUIDKey = taskId + ':' + TASK_SUCCESS_USER_ID;
    }

    @Override
    public String taskId() {
        return taskId;
    }

    @Override
    public int qps() {
        Integer count = cacheManager.hget(taskKey, TASK_QPS_FIELD, Integer.TYPE);
        return count == null ? 1000 : count;
    }

    @Override
    public void updateQps(int qps) {
        cacheManager.hset(taskKey, TASK_QPS_FIELD, qps);
    }

    @Override
    public boolean isDone() {
        return Boolean.TRUE.equals(cacheManager.hget(taskKey, TASK_DONE_FIELD, Boolean.class));
    }

    @Override
    public int sendCount() {
        Integer count = cacheManager.hget(taskKey, TASK_SEND_COUNT_FIELD, Integer.TYPE);
        return count == null ? 0 : count;
    }

    @Override
    public void cancel() {
        cacheManager.hset(taskKey, TASK_CANCEL_FIELD, 1);
    }

    @Override
    public boolean isCancelled() {
        Integer status = cacheManager.hget(taskKey, TASK_CANCEL_FIELD, Integer.TYPE);
        return status != null && status == 1;
    }

    @Override
    public int incSendCount(int count) {
        return (int) cacheManager.hincrBy(taskKey, TASK_SEND_COUNT_FIELD, count);
    }

    public void success(String... userIds) {
        cacheManager.lpush(taskSuccessUIDKey, userIds);
    }

    public List<String> successUserIds() {
        return cacheManager.lrange(taskSuccessUIDKey, 0, -1, String.class);
    }
}
