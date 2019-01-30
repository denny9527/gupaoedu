

package com.gupao.edu.vip.lion.api.common;

import com.gupao.edu.vip.lion.api.event.*;
import com.gupao.edu.vip.lion.api.spi.Plugin;


public interface ServerEventListener extends Plugin {

    /**
     * 该事件通过guava EventBus发出，实现接口的方法必须增加
     *
     * <code>@Subscribe 和 @AllowConcurrentEvents</code>注解，
     * 并在构造函数调用EventBus.register(this);
     */
    default void on(ServerStartupEvent event) {
    }

    /**
     * 该事件通过guava EventBus发出，实现接口的方法必须增加
     *
     * <code>@Subscribe 和 @AllowConcurrentEvents</code>注解，
     * 并在构造函数调用EventBus.register(this);
     */
    default void on(ServerShutdownEvent server) {
    }

    /**
     * 该事件通过guava EventBus发出，实现接口的方法必须增加
     *
     * <code>@Subscribe 和 @AllowConcurrentEvents</code>注解，
     * 并在构造函数调用EventBus.register(this);
     */
    default void on(RouterChangeEvent event) {
    }

    /**
     * 该事件通过guava EventBus发出，实现接口的方法必须增加
     *
     * <code>@Subscribe 和 @AllowConcurrentEvents</code>注解，
     * 并在构造函数调用EventBus.register(this);
     */
    default void on(KickUserEvent event) {
    }

    /**
     * 该事件通过guava EventBus发出，实现接口的方法必须增加
     *
     * <code>@Subscribe 和 @AllowConcurrentEvents</code>注解，
     * 并在构造函数调用EventBus.register(this);
     */
    default void on(UserOnlineEvent event) {
    }

    /**
     * 该事件通过guava EventBus发出，实现接口的方法必须增加
     *
     * <code>@Subscribe 和 @AllowConcurrentEvents</code>注解，
     * 并在构造函数调用EventBus.register(this);
     */
    default void on(UserOfflineEvent event) {
    }
}
