package org.machinemc.paklet.test;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import org.machinemc.paklet.*;
import org.machinemc.paklet.netty.NettyDataVisitor;
import org.machinemc.paklet.processors.*;
import org.machinemc.paklet.test.packet.ArrayPacket;
import org.machinemc.paklet.test.packet.TestCustomLogicCustomPacket;
import org.machinemc.paklet.test.packet.TestPacket;

import java.util.Arrays;
import java.util.Objects;

public class ProcessorsTest {

    @Test
    public void pluginTest() {
        assert ProcessorsUtil.isGeneratedPacketClass(TestCustomLogicCustomPacket.class);
        assert ProcessorsUtil.isGeneratedPacketClass(TestPacket.class);
    }

    @Test
    public void processorsBytecodeTest() {
        PacketReader<TestCustomLogicCustomPacket> cReader = new CustomReaderCreator().create(TestCustomLogicCustomPacket.class);
        PacketWriter<TestCustomLogicCustomPacket> cWriter = new CustomWriterCreator().create(TestCustomLogicCustomPacket.class);
        PacketReader<TestPacket> gReader = new GeneratedReaderCreator().create(TestPacket.class);
        PacketWriter<TestPacket> gWriter = new GeneratedWriterCreator().create(TestPacket.class);
    }

    @Test
    public void basicTest() {
        PacketFactory factory = TestUtil.createFactory();

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

        TestCustomLogicCustomPacket testCustomLogicPacket = new TestCustomLogicCustomPacket();
        testCustomLogicPacket.value = 20;
        factory.write(testCustomLogicPacket, visitor);
        TestCustomLogicCustomPacket testCustomLogicPacketClone = factory.create(Packet.DEFAULT, visitor);

        assert testCustomLogicPacketClone.value == testCustomLogicPacket.value;
    }

    @Test
    public void arrayTest() {
        PacketFactory factory = TestUtil.createFactory();

        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());

        ArrayPacket arrayPacket = new ArrayPacket();
        arrayPacket.stringArray = new String[]{"Hello", "World", "foo"};
        arrayPacket.nestedArray = new String[][]{new String[]{"Hello", "World"}, new String[]{"foo", "bar"}};
        arrayPacket.optionalElements = new String[]{"Hello", "optional", null};

        factory.write(arrayPacket, visitor);
        ArrayPacket arrayPacketClone = factory.create(Packet.DEFAULT, visitor);

        assert Arrays.compare(arrayPacketClone.stringArray, arrayPacket.stringArray) == 0;
        assert Arrays.compare(arrayPacketClone.nestedArray[0], arrayPacket.nestedArray[0]) == 0;
        assert Arrays.compare(arrayPacketClone.nestedArray[1], arrayPacket.nestedArray[1]) == 0;
        assert Arrays.compare(arrayPacketClone.optionalElements, arrayPacket.optionalElements) == 0;
    }

}
