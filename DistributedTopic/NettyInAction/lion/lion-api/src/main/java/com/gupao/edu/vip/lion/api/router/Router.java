
package com.gupao.edu.vip.lion.api.router;


public interface Router<T> {

    T getRouteValue();

    RouterType getRouteType();

    enum RouterType {
        LOCAL, REMOTE
    }

}
