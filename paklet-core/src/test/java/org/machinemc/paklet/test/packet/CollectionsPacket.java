package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.Packet;
import org.machinemc.paklet.modifiers.Optional;
import org.machinemc.paklet.test.TestPackets;

import java.util.HashSet;
import java.util.List;

@Packet(id = 2, catalogue = TestPackets.class)
public class CollectionsPacket {

    public List<@Optional String> contents;
    public HashSet<Integer> numbers;
    public List<List<String>> nestedCollection;

}
