
package com.gupao.edu.vip.lion.api.event;

import com.gupao.edu.vip.lion.api.router.Router;


public final class RouterChangeEvent implements Event {
    public final String userId;
    public final Router<?> router;

    public RouterChangeEvent(String userId, Router<?> router) {
        this.userId = userId;
        this.router = router;
    }
}
