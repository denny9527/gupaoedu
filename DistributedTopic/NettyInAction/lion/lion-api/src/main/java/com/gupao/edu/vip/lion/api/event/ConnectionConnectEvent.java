

package com.gupao.edu.vip.lion.api.event;

import com.gupao.edu.vip.lion.api.connection.Connection;


public final class ConnectionConnectEvent implements Event {
    public final Connection connection;

    public ConnectionConnectEvent(Connection connection) {
        this.connection = connection;
    }
}
