package org.machinemc.paklet.test;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import org.machinemc.paklet.*;
import org.machinemc.paklet.netty.NettyDataVisitor;
import org.machinemc.paklet.processors.*;
import org.machinemc.paklet.serializers.SerializerProvider;
import org.machinemc.paklet.serializers.Serializers;
import org.machinemc.paklet.test.packet.TestCustomLogicPacket;
import org.machinemc.paklet.test.packet.TestPacket;

import java.util.Objects;

public class ProcessorsTest {

    @Test
    public void testPlugin() {
        assert ProcessorsUtil.isGeneratedPacketClass(TestCustomLogicPacket.class);
        assert ProcessorsUtil.isGeneratedPacketClass(TestPacket.class);
    }

    @Test
    public void testProcessorsBytecode() {
        PacketReader<TestCustomLogicPacket> cReader = new CustomReaderCreator().create(TestCustomLogicPacket.class);
        PacketWriter<TestCustomLogicPacket> cWriter = new CustomWriterCreator().create(TestCustomLogicPacket.class);
        PacketReader<TestPacket> gReader = new GeneratedReaderCreator().create(TestPacket.class);
        PacketWriter<TestPacket> gWriter = new GeneratedWriterCreator().create(TestPacket.class);
    }

    @Test
    public void factoryTest() {
        SerializerProvider provider = SerializerProviderBuilder.create().loadProvided().build();
        PacketFactory factory = PacketFactoryBuilder.create(new Serializers.Integer(), provider).loadDefaults().build();

        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());

        TestPacket testPacket = new TestPacket();
        testPacket.height = 20;
        testPacket.name = "Foo";
        testPacket.value = 5;
        factory.write(testPacket, visitor);
        TestPacket testPacketClone = factory.create(Packet.DEFAULT, visitor);

        assert testPacketClone.height == testPacket.height;
        assert Objects.equals(testPacketClone.name, testPacket.name);
        assert testPacketClone.value == testPacket.value;

        TestCustomLogicPacket testCustomLogicPacket = new TestCustomLogicPacket();
        testCustomLogicPacket.value = 20;
        factory.write(testCustomLogicPacket, visitor);
        TestCustomLogicPacket testCustomLogicPacketClone = factory.create(Packet.DEFAULT, visitor);

        assert testCustomLogicPacketClone.value == testCustomLogicPacket.value;
    }

}
