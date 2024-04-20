package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.Packet;
import org.machinemc.paklet.test.TestPackets;

@Packet(id = 10, catalogue = TestPackets.class)
public class TestPacket {

    public String name;
    public int value;
    public double height;

}
