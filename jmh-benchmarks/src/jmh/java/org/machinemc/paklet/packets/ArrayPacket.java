package org.machinemc.paklet.packets;

import org.machinemc.paklet.BenchmarkPackets;
import org.machinemc.paklet.Packet;

@Packet(id = 1, catalogue = BenchmarkPackets.class)
public class ArrayPacket {

    public int[] intArray;
    public String[][] nestedStringArray;
    public double[] doubleArray;

}
