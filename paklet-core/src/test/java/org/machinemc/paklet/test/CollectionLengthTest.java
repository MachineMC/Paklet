package org.machinemc.paklet.test;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.Packet;
import org.machinemc.paklet.PacketFactory;
import org.machinemc.paklet.netty.NettyDataVisitor;
import org.machinemc.paklet.test.packet.NoCollectionLengthPacket;

import java.util.Arrays;

public class CollectionLengthTest {

    @Test
    public void doNotPrefixTest() {
        PacketFactory factory = TestUtil.createFactory();

        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());

        NoCollectionLengthPacket packet = new NoCollectionLengthPacket();
        packet.signature = new byte[256];
        packet.signature[0] = 1;
        packet.signature[1] = 2;
        packet.signature[2] = 3;

        factory.write(packet, Packet.DEFAULT, visitor);

        assert visitor.writerIndex() == 257;

        NoCollectionLengthPacket packetClone = factory.create(Packet.DEFAULT, visitor);

        assert packetClone.signature.length == 256;
        assert Arrays.equals(packetClone.signature, packet.signature);
    }

}
