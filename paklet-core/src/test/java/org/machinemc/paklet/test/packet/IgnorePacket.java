package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.Packet;
import org.machinemc.paklet.modifiers.Ignore;
import org.machinemc.paklet.test.TestPackets;

@Packet(id = 3, catalogue = TestPackets.class)
public class IgnorePacket {

    @Ignore
    public String ignore = "Hello";

    public int value;

}
