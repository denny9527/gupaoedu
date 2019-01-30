
package com.gupao.edu.vip.lion.api.message;

import com.gupao.edu.vip.lion.api.connection.Connection;
import com.gupao.edu.vip.lion.api.protocol.Packet;


public interface PacketReceiver {
    void onReceive(Packet packet, Connection connection);
}
