package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.Packet;

@Packet(0x80)
public class TestPacket {

    public String name;
    public int value;
    public double height;

}
