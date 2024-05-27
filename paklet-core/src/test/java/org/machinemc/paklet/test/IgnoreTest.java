package org.machinemc.paklet.test;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import org.machinemc.paklet.*;
import org.machinemc.paklet.netty.NettyDataVisitor;
import org.machinemc.paklet.test.packet.IgnorePacket;

public class IgnoreTest {

    @Test
    public void ignoreTest() {
        PacketFactory factory = TestUtil.createFactory();

        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());

        IgnorePacket packet = new IgnorePacket();
        packet.ignore = "Foo";
        packet.value = 10;

        factory.write(packet, Packet.DEFAULT, visitor);
        IgnorePacket packetClone = factory.create(Packet.DEFAULT, visitor);

        assert packetClone.value == packet.value;
        assert !packet.ignore.equals(packetClone.ignore);
        assert packetClone.ignore.equals(new IgnorePacket().ignore);
    }

}
