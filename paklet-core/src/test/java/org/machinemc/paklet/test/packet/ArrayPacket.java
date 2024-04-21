package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.Packet;
import org.machinemc.paklet.modifiers.Optional;
import org.machinemc.paklet.test.TestPackets;

@Packet(id = 1, catalogue = TestPackets.class)
public class ArrayPacket {

    public String[] stringArray;
    public String[][] nestedArray;
    public @Optional String[] optionalElements;

}
