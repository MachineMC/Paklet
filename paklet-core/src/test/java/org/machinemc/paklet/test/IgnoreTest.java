package org.machinemc.paklet.test;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import org.machinemc.paklet.*;
import org.machinemc.paklet.netty.NettyDataVisitor;
import org.machinemc.paklet.serializers.SerializerProvider;
import org.machinemc.paklet.serializers.Serializers;
import org.machinemc.paklet.test.packet.IgnorePacket;

public class IgnoreTest {

    @Test
    public void ignoreTest() {
        SerializerProvider provider = serializerProvider();
        PacketFactory factory = factory(provider);

        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());

        IgnorePacket packet = new IgnorePacket();
        packet.ignore = "Foo";
        packet.value = 10;

        factory.write(packet, visitor);
        IgnorePacket packetClone = factory.create(Packet.DEFAULT, visitor);

        assert packetClone.value == packet.value;
        assert !packet.ignore.equals(packetClone.ignore);
        assert packetClone.ignore.equals(new IgnorePacket().ignore);
        assert visitor.writerIndex() == 8; // 4 for length, 4 for IgnorePacket#value
    }

    private SerializerProvider serializerProvider() {
        return SerializerProviderBuilder.create().loadProvided().build();
    }

    private PacketFactory factory(SerializerProvider serializerProvider) {
        return PacketFactoryBuilder.create(new Serializers.Integer(), serializerProvider).loadDefaults().build();
    }

}
