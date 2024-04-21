package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.Packet;
import org.machinemc.paklet.PacketID;
import org.machinemc.paklet.test.TestPackets;

import java.util.Random;

@Packet(id = Packet.DYNAMIC_PACKET, catalogue = TestPackets.class)
public class CustomIDPacket {

    @PacketID
    private static final int packetID;

    static {
        packetID = new Random().nextInt(4, 5);
    }

    public String message;

}
