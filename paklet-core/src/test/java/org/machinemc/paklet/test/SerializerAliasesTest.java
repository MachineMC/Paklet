package org.machinemc.paklet.test;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.Packet;
import org.machinemc.paklet.PacketFactory;
import org.machinemc.paklet.netty.NettyDataVisitor;
import org.machinemc.paklet.test.packet.AliasPacket;

public class SerializerAliasesTest {

    @Test
    public void aliasesTest() {
        PacketFactory factory = TestUtil.createFactory();

        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());

        AliasPacket packet = new AliasPacket();
        packet.foo = 1;

        factory.write(packet, visitor);
        AliasPacket packetClone = factory.create(Packet.DEFAULT, visitor);

        assert packetClone.foo == packet.foo;
        assert visitor.writerIndex() == 2; // 1 for packet ID, 1 for foo field; both VarInts
    }

}
