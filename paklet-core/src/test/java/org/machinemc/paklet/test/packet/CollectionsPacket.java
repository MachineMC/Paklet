package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.Packet;
import org.machinemc.paklet.modifiers.Optional;

import java.util.HashSet;
import java.util.List;

@Packet(2)
public class CollectionsPacket {

    public List<@Optional String> contents;
    public HashSet<Integer> numbers;
    public List<List<String>> nestedCollection;

}
