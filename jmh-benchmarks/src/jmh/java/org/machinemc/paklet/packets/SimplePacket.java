package org.machinemc.paklet.packets;

import org.machinemc.paklet.BenchmarkPackets;
import org.machinemc.paklet.Packet;

@Packet(id = 0, catalogue = BenchmarkPackets.class)
public class SimplePacket {

    public double f1;
    public boolean f2;
    public int f3;
    public String f4;
    public float f5;

}
