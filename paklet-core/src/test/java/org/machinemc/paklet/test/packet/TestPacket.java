package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.Packet;
import org.machinemc.paklet.test.TestPackets;

@Packet(id = 0x80, catalogue = TestPackets.class)
public class TestPacket {

    public String name;
    public int value;
    public double height;

}
