
package com.gupao.edu.vip.lion.common.router;

/**
 *
 *
 */
public class MQKickRemoteMsg implements KickRemoteMsg {
    private String userId;
    private String deviceId;
    private String connId;
    private int clientType;
    private String targetServer;
    private int targetPort;

    public MQKickRemoteMsg setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public MQKickRemoteMsg setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public MQKickRemoteMsg setConnId(String connId) {
        this.connId = connId;
        return this;
    }

    public MQKickRemoteMsg setClientType(int clientType) {
        this.clientType = clientType;
        return this;
    }

    public MQKickRemoteMsg setTargetServer(String targetServer) {
        this.targetServer = targetServer;
        return this;
    }

    public MQKickRemoteMsg setTargetPort(int targetPort) {
        this.targetPort = targetPort;
        return this;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public String getConnId() {
        return connId;
    }

    @Override
    public int getClientType() {
        return clientType;
    }

    @Override
    public String getTargetServer() {
        return targetServer;
    }

    @Override
    public int getTargetPort() {
        return targetPort;
    }

    @Override
    public String toString() {
        return "KickRemoteMsg{"
                + "userId='" + userId + '\''
                + ", deviceId='" + deviceId + '\''
                + ", connId='" + connId + '\''
                + ", clientType='" + clientType + '\''
                + ", targetServer='" + targetServer + '\''
                + ", targetPort=" + targetPort
                + '}';
    }
}
