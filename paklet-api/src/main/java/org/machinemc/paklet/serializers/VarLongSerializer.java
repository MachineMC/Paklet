package org.machinemc.paklet.serializers;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.Serializer;

@Supports({Long.class, long.class})
public class VarLongSerializer implements Serializer<Long> {

    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    @Override
    public void serialize(DataVisitor visitor, Long value) {
        while (true) {
            if ((value & ~((long) SEGMENT_BITS)) == 0) {
                visitor.writeByte(value.byteValue());
                return;
            }
            visitor.writeByte((byte) ((value & SEGMENT_BITS) | CONTINUE_BIT));
            value >>>= 7;
        }
    }

    @Override
    public Long deserialize(DataVisitor visitor) {
        long value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = visitor.readByte();
            value |= (long) (currentByte & SEGMENT_BITS) << position;
            if ((currentByte & CONTINUE_BIT) == 0) break;
            position += 7;
            if (position >= 64) throw new RuntimeException("VarLong is too big");
        }

        return value;
    }

}
