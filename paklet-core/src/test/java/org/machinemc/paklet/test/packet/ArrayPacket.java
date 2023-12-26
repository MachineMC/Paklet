package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.Packet;
import org.machinemc.paklet.modifiers.Optional;

@Packet(1)
public class ArrayPacket {

    public String[] stringArray;
    public String[][] nestedArray;
    public @Optional String[] optionalElements;

}
