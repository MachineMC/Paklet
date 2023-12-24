package org.machinemc.paklet.serializers;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.Serializer;

@Supports({Integer.class, int.class})
public class VarIntSerializer implements Serializer<Integer> {

    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    @Override
    public void serialize(DataVisitor visitor, Integer value) {
        while (true) {
            if ((value & ~SEGMENT_BITS) == 0) {
                visitor.writeByte(value.byteValue());
                return;
            }
            visitor.writeByte((byte) ((value & SEGMENT_BITS) | CONTINUE_BIT));
            value >>>= 7;
        }
    }

    @Override
    public Integer deserialize(DataVisitor visitor) {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = visitor.readByte();
            value |= (currentByte & SEGMENT_BITS) << position;
            if ((currentByte & CONTINUE_BIT) == 0) break;
            position += 7;
            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }

        return value;
    }

}
