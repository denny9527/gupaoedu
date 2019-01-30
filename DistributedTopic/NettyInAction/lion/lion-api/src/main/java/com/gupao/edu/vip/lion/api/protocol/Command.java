
package com.gupao.edu.vip.lion.api.protocol;


public enum Command {
    HEARTBEAT(1),
    HANDSHAKE(2),
    LOGIN(3),
    LOGOUT(4),
    BIND(5),
    UNBIND(6),
    FAST_CONNECT(7),
    PAUSE(8),
    RESUME(9),
    ERROR(10),
    OK(11),
    HTTP_PROXY(12),
    KICK(13),
    GATEWAY_KICK(14),
    PUSH(15),
    GATEWAY_PUSH(16),
    NOTIFICATION(17),
    GATEWAY_NOTIFICATION(18),
    CHAT(19),
    GATEWAY_CHAT(20),
    GROUP(21),
    GATEWAY_GROUP(22),
    ACK(23),
    NACK(24),
    UNKNOWN(-1);

    Command(int cmd) {
        this.cmd = (byte) cmd;
    }

    public final byte cmd;

    public static Command toCMD(byte b) {
        Command[] values = values();
        if (b > 0 && b < values.length) return values[b - 1];
        return UNKNOWN;
    }
}
