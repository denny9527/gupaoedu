

package com.gupao.edu.vip.lion.api.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;


public abstract class BaseService implements Service {

    protected final AtomicBoolean started = new AtomicBoolean();

    @Override
    public void init() {
    }

    @Override
    public boolean isRunning() {
        return started.get();
    }

    protected void tryStart(Listener l, FunctionEx function) {
        FutureListener listener = wrap(l);
        if (started.compareAndSet(false, true)) {
            try {
                init();
                function.apply(listener);
                listener.monitor(this);//主要用于异步，否则应该放置在function.apply(listener)之前
            } catch (Throwable e) {
                listener.onFailure(e);
                throw new ServiceException(e);
            }
        } else {
            if (throwIfStarted()) {
                listener.onFailure(new ServiceException("service already started."));
            } else {
                listener.onSuccess();
            }
        }
    }

    protected void tryStop(Listener l, FunctionEx function) {
        FutureListener listener = wrap(l);
        if (started.compareAndSet(true, false)) {
            try {
                function.apply(listener);
                listener.monitor(this);//主要用于异步，否则应该放置在function.apply(listener)之前
            } catch (Throwable e) {
                listener.onFailure(e);
                throw new ServiceException(e);
            }
        } else {
            if (throwIfStopped()) {
                listener.onFailure(new ServiceException("service already stopped."));
            } else {
                listener.onSuccess();
            }
        }
    }

    public final CompletableFuture<Boolean> start() {
        FutureListener listener = new FutureListener(started);
        start(listener);
        return listener;
    }

    public final CompletableFuture<Boolean> stop() {
        FutureListener listener = new FutureListener(started);
        stop(listener);
        return listener;
    }

    @Override
    public final boolean syncStart() {
        return start().join();
    }

    @Override
    public final boolean syncStop() {
        return stop().join();
    }

    @Override
    public void start(Listener listener) {
        tryStart(listener, this::doStart);
    }

    @Override
    public void stop(Listener listener) {
        tryStop(listener, this::doStop);
    }

    protected void doStart(Listener listener) throws Throwable {
        listener.onSuccess();
    }

    protected void doStop(Listener listener) throws Throwable {
        listener.onSuccess();
    }

    /**
     * 控制当服务已经启动后，重复调用start方法，是否抛出服务已经启动异常
     * 默认是true
     *
     * @return true:抛出异常
     */
    protected boolean throwIfStarted() {
        return true;
    }

    /**
     * 控制当服务已经停止后，重复调用stop方法，是否抛出服务已经停止异常
     * 默认是true
     *
     * @return true:抛出异常
     */
    protected boolean throwIfStopped() {
        return true;
    }

    /**
     * 服务启动停止，超时时间, 默认是10s
     *
     * @return 超时时间
     */
    protected int timeoutMillis() {
        return 1000 * 10;
    }

    protected interface FunctionEx {
        void apply(Listener l) throws Throwable;
    }

    /**
     * 防止Listener被重复执行
     *
     * @param l listener
     * @return FutureListener
     */
    public FutureListener wrap(Listener l) {
        if (l == null) return new FutureListener(started);
        if (l instanceof FutureListener) return (FutureListener) l;
        return new FutureListener(l, started);
    }
}
