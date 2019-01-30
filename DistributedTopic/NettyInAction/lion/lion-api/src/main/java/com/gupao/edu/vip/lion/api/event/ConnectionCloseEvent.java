

package com.gupao.edu.vip.lion.api.event;

import com.gupao.edu.vip.lion.api.connection.Connection;

public final class ConnectionCloseEvent implements Event {
    public final Connection connection;


    public ConnectionCloseEvent(Connection connection) {
        this.connection = connection;
    }
}
