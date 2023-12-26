package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.Packet;
import org.machinemc.paklet.modifiers.Ignore;

@Packet(3)
public class IgnorePacket {

    @Ignore
    public String ignore = "Hello";

    public int value;

}
