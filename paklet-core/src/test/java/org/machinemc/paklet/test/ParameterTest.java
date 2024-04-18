package org.machinemc.paklet.test;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import org.machinemc.paklet.*;
import org.machinemc.paklet.netty.NettyDataVisitor;
import org.machinemc.paklet.test.packet.CollectionsPacket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ParameterTest {

    @Test
    public void parameterTest() {
        PacketFactory factory = TestUtil.createFactory();

        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());

        CollectionsPacket packet = new CollectionsPacket();
        packet.contents = new ArrayList<>();
        packet.contents.add("Blob");
        packet.contents.add(null);
        packet.contents.add("Foo");
        packet.numbers = new HashSet<>(List.of(1, 2, 3, 4, 5));
        packet.nestedCollection = new ArrayList<>(List.of(List.of("Hello"), List.of("World")));

        factory.write(packet, visitor);
        CollectionsPacket packetClone = factory.create(Packet.DEFAULT, visitor);

        assert packetClone.contents.equals(packet.contents);
        assert packetClone.numbers.equals(packet.numbers);
        assert packetClone.nestedCollection.equals(packet.nestedCollection);
    }

}
