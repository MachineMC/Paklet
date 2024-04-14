package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.Packet;
import org.machinemc.paklet.PacketID;

import java.util.Random;

@Packet(Packet.DYNAMIC_PACKET)
public class CustomIDPacket {

    @PacketID
    private static final int packetID;

    static {
        packetID = new Random().nextInt(4, 5);
    }

    public String message;

}
