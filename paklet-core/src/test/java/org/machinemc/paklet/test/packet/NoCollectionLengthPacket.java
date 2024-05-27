package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.Packet;
import org.machinemc.paklet.metadata.DoNotPrefix;
import org.machinemc.paklet.metadata.FixedLength;
import org.machinemc.paklet.test.TestPackets;

@Packet(id = 11, catalogue = TestPackets.class)
public class NoCollectionLengthPacket {

    public byte @FixedLength(256) @DoNotPrefix [] signature;

}
