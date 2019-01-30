
package com.gupao.edu.vip.lion.bootstrap.job;

import com.gupao.edu.vip.lion.tools.log.Logs;

import java.util.function.Supplier;

/**
 */
public abstract class BootJob {
    protected BootJob next;

    protected abstract void start();

    protected abstract void stop();

    public void startNext() {
        if (next != null) {
            Logs.Console.info("start bootstrap job [{}]", getNextName());
            next.start();
        }
    }

    public void stopNext() {
        if (next != null) {
            next.stop();
            Logs.Console.info("stopped bootstrap job [{}]", getNextName());
        }
    }

    public BootJob setNext(BootJob next) {
        this.next = next;
        return next;
    }

    public BootJob setNext(Supplier<BootJob> next, boolean enabled) {
        if (enabled) {
            return setNext(next.get());
        }
        return this;
    }

    protected String getNextName() {
        return next.getName();
    }

    protected String getName() {
        return this.getClass().getSimpleName();
    }
}
