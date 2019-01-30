
package com.gupao.edu.vip.lion.common.memory;

import com.gupao.edu.vip.lion.api.protocol.Command;
import com.gupao.edu.vip.lion.api.protocol.Packet;
import com.gupao.edu.vip.lion.api.protocol.UDPPacket;
import com.gupao.edu.vip.lion.tools.config.CC;

/**
 */
public interface PacketFactory {
    PacketFactory FACTORY = CC.lion.net.udpGateway() ? UDPPacket::new : Packet::new;

    static Packet get(Command command) {
        return FACTORY.create(command);
    }

    Packet create(Command command);
}