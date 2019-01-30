
package com.gupao.edu.vip.lion.api.push;

import com.gupao.edu.vip.lion.api.protocol.Packet;


public enum AckModel {
    NO_ACK((byte) 0),//不需要ACK
    AUTO_ACK(Packet.FLAG_AUTO_ACK),//客户端收到消息后自动确认消息
    BIZ_ACK(Packet.FLAG_BIZ_ACK);//由客户端业务自己确认消息是否到达
    public final byte flag;

    AckModel(byte flag) {
        this.flag = flag;
    }
}
