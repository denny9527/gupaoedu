
package com.gupao.edu.vip.lion.core.router;

import com.gupao.edu.vip.lion.api.connection.Connection;
import com.gupao.edu.vip.lion.api.router.Router;


public final class LocalRouter implements Router<Connection> {
    private final Connection connection;

    public LocalRouter(Connection connection) {
        this.connection = connection;
    }

    public int getClientType() {
        return connection.getSessionContext().getClientType();
    }

    @Override
    public Connection getRouteValue() {
        return connection;
    }

    @Override
    public RouterType getRouteType() {
        return RouterType.LOCAL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalRouter that = (LocalRouter) o;

        return getClientType() == that.getClientType();

    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getClientType());
    }

    @Override
    public String toString() {
        return "LocalRouter{" + connection + '}';
    }
}
