package org.machinemc.paklet.test;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import org.machinemc.paklet.*;
import org.machinemc.paklet.netty.NettyDataVisitor;
import org.machinemc.paklet.test.packet.CustomIDPacket;
import org.machinemc.paklet.test.packet.RulesTestingPacket;

import java.time.Instant;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class SerializationTest {

    @Test
    public void arrayTest() {
        PacketFactory factory = TestUtil.createFactory();

        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());

        RulesTestingPacket packet = new RulesTestingPacket();
        packet.date = Date.from(Instant.now());
        packet.currency = Currency.getInstance(Locale.US);
        packet.state = RulesTestingPacket.State.COMPLETED;

        factory.write(packet, Packet.DEFAULT, visitor);
        RulesTestingPacket packetClone = factory.create(Packet.DEFAULT, visitor);

        assert packetClone.date.equals(packet.date);
        assert packetClone.currency.equals(packet.currency);
        assert packetClone.state == packet.state;
    }

    @Test
    public void dynamicPacketIDTest() {
        PacketFactory factory = TestUtil.createFactory();

        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());

        CustomIDPacket packet = new CustomIDPacket();
        packet.message = "Hello world";

        factory.write(packet, Packet.DEFAULT, visitor);
        CustomIDPacket packetClone = factory.create(Packet.DEFAULT, visitor);

        assert packet.message.equals(packetClone.message);
    }

}
