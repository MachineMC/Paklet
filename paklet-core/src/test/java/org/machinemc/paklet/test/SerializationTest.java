package org.machinemc.paklet.test;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import org.machinemc.paklet.*;
import org.machinemc.paklet.netty.NettyDataVisitor;
import org.machinemc.paklet.serializers.SerializerProvider;
import org.machinemc.paklet.serializers.Serializers;
import org.machinemc.paklet.test.packet.RulesTestingPacket;

import java.time.Instant;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class SerializationTest {

    @Test
    public void arrayTest() {
        SerializerProvider provider = serializerProvider();
        PacketFactory factory = factory(provider);

        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());

        RulesTestingPacket packet = new RulesTestingPacket();
        packet.date = Date.from(Instant.now());
        packet.currency = Currency.getInstance(Locale.US);
        packet.state = RulesTestingPacket.State.COMPLETED;

        factory.write(packet, visitor);
        RulesTestingPacket packetClone = factory.create(Packet.DEFAULT, visitor);

        assert packetClone.date.equals(packet.date);
        assert packetClone.currency.equals(packet.currency);
        assert packetClone.state == packet.state;
    }

    private SerializerProvider serializerProvider() {
        return SerializerProviderBuilder.create().loadProvided().build();
    }

    private PacketFactory factory(SerializerProvider serializerProvider) {
        return PacketFactoryBuilder.create(new Serializers.Integer(), serializerProvider).loadDefaults().build();
    }

}
