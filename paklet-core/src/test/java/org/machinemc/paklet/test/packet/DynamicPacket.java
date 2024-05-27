package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.Packet;
import org.machinemc.paklet.PacketID;
import org.machinemc.paklet.PacketRegistrationContext;
import org.machinemc.paklet.test.TestPackets;

@Packet(id = Packet.DYNAMIC_PACKET, group = {"one", "two", "three"}, catalogue = TestPackets.class)
public class DynamicPacket {

    @PacketID
    private static int id() {
        String group = PacketRegistrationContext.get().getPacketGroup();
        return switch (group) {
            case "one" -> 21;
            case "two" -> 22;
            case "three" -> 23;
            default -> Packet.INVALID_PACKET;
        };
    }

    public int value;

}
