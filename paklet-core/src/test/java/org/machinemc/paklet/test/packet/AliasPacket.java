package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.Packet;
import org.machinemc.paklet.serialization.aliases.VarInt;
import org.machinemc.paklet.test.TestPackets;

@Packet(id = 7, catalogue = TestPackets.class)
public class AliasPacket {

    public @VarInt int foo;

}
