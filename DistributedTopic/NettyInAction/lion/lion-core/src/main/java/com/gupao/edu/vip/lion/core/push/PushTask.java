
package com.gupao.edu.vip.lion.core.push;

import java.util.concurrent.ScheduledExecutorService;


public interface PushTask extends Runnable {
    ScheduledExecutorService getExecutor();
}
