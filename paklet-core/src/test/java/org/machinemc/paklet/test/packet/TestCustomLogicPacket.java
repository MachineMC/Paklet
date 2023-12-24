package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.Packet;
import org.machinemc.paklet.PacketLogic;

@Packet(0x50)
public class TestCustomLogicPacket implements PacketLogic {

    public byte value;

    @Override
    public void construct(DataVisitor visitor) {
        value = (byte) visitor.readInt();
    }

    @Override
    public void deconstruct(DataVisitor visitor) {
        visitor.writeInt(value);
    }

}
