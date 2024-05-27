package org.machinemc.paklet.test;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.Packet;
import org.machinemc.paklet.PacketFactory;
import org.machinemc.paklet.netty.NettyDataVisitor;
import org.machinemc.paklet.test.packet.CustomSerializerPacket;

public class CustomSerializerTest {

    @Test
    public void customSerializerTest() {
        PacketFactory factory = TestUtil.createFactory();

        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());

        CustomSerializerPacket packet = new CustomSerializerPacket();
        packet.content = "Hello World";

        factory.write(packet, Packet.DEFAULT, visitor);
        CustomSerializerPacket packetClone = factory.create(Packet.DEFAULT, visitor);

        assert packetClone.content.equals(packet.content);
    }

}
