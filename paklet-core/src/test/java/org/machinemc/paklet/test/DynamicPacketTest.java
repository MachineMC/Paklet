package org.machinemc.paklet.test;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.PacketFactory;
import org.machinemc.paklet.netty.NettyDataVisitor;
import org.machinemc.paklet.serialization.VarIntSerializer;
import org.machinemc.paklet.test.packet.DynamicPacket;

public class DynamicPacketTest {

    @Test
    public void testDynamicPacketIDs() {
        PacketFactory factory = TestUtil.createFactory();

        assert factory.getPacketID(DynamicPacket.class, "one") == 21;
        assert factory.getPacketID(DynamicPacket.class, "two") == 22;
        assert factory.getPacketID(DynamicPacket.class, "three") == 23;

        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());

        DynamicPacket packet = new DynamicPacket();
        packet.value = 15;

        factory.write(packet, "two", visitor);

        assert visitor.read(null, new VarIntSerializer()) == 22;

        visitor.readerIndex(0);

        DynamicPacket packetClone = factory.create("two", visitor);

        assert packetClone.value == packet.value;
    }

}
