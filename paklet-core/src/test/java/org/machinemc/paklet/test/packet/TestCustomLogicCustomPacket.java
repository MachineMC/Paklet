package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.Packet;
import org.machinemc.paklet.CustomPacket;
import org.machinemc.paklet.serialization.SerializerContext;
import org.machinemc.paklet.test.TestPackets;

@Packet(id = 9, catalogue = TestPackets.class)
public class TestCustomLogicCustomPacket extends ExtensionTest implements CustomPacket {

    public byte value;

    @Override
    public void construct(SerializerContext context, DataVisitor visitor) {
        value = (byte) visitor.readInt();
    }

    @Override
    public void deconstruct(SerializerContext context, DataVisitor visitor) {
        visitor.writeInt(value);
    }

}

class ExtensionTest {
}
