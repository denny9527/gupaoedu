
package com.gupao.edu.vip.lion.tools.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class NamedPoolThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNum = new AtomicInteger(1);

    private final AtomicInteger threadNum = new AtomicInteger(1);

    private final ThreadGroup group;
    private final String namePre;
    private final boolean isDaemon;

    public NamedPoolThreadFactory(String prefix) {
        this(prefix, true);
    }

    public NamedPoolThreadFactory(String prefix, boolean daemon) {
        SecurityManager manager = System.getSecurityManager();
        if (manager != null) {
            group = manager.getThreadGroup();
        } else {
            group = Thread.currentThread().getThreadGroup();
        }
        isDaemon = daemon;
        namePre = prefix + "-p-" + poolNum.getAndIncrement() + "-t-";
    }

    /**
     * stackSize - 新线程的预期堆栈大小，为零时表示忽略该参数
     */
    @Override
    public Thread newThread(Runnable runnable) {
        Thread t = new Thread(group, runnable, namePre + threadNum.getAndIncrement(), 0);
        t.setContextClassLoader(NamedPoolThreadFactory.class.getClassLoader());
        t.setPriority(Thread.NORM_PRIORITY);
        t.setDaemon(isDaemon);
        return t;
    }

}
