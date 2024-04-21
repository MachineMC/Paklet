package org.machinemc.paklet.packets;

import org.machinemc.paklet.BenchmarkPackets;
import org.machinemc.paklet.Packet;

import java.util.Collection;
import java.util.List;

@Packet(id = 3, catalogue = BenchmarkPackets.class)
public class CollectionPacket {

    public List<String> stringList;
    public Collection<Integer> integerCollection;
    public List<List<String>> nestedStringList;

}
