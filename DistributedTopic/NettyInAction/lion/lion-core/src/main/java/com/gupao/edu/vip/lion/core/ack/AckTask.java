
package com.gupao.edu.vip.lion.core.ack;

import java.util.concurrent.Future;


public final class AckTask implements Runnable {
    private final int ackMessageId;
    private AckTaskQueue ackTaskQueue;
    private AckCallback callback;
    private Future<?> timeoutFuture;

    public AckTask(int ackMessageId) {
        this.ackMessageId = ackMessageId;
    }

    public static AckTask from(int ackMessageId) {
        return new AckTask(ackMessageId);
    }

    public AckTask setAckTaskQueue(AckTaskQueue ackTaskQueue) {
        this.ackTaskQueue = ackTaskQueue;
        return this;
    }

    public void setFuture(Future<?> future) {
        this.timeoutFuture = future;
    }

    public AckTask setCallback(AckCallback callback) {
        this.callback = callback;
        return this;
    }

    public int getAckMessageId() {
        return ackMessageId;
    }


    private boolean tryDone() {
        return timeoutFuture.cancel(true);
    }

    public void onResponse() {
        if (tryDone()) {
            callback.onSuccess(this);
            callback = null;
        }
    }

    public void onTimeout() {
        AckTask context = ackTaskQueue.getAndRemove(ackMessageId);
        if (context != null && tryDone()) {
            callback.onTimeout(this);
            callback = null;
        }
    }

    @Override
    public String toString() {
        return "{" +
                ", ackMessageId=" + ackMessageId +
                '}';
    }

    @Override
    public void run() {
        onTimeout();
    }
}
