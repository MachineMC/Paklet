package org.machinemc.paklet.serialization;

import org.machinemc.paklet.DataVisitor;

/**
 * Serializer for variable-length longs.
 * <p>
 * Variable-length format such that smaller numbers use fewer bytes.
 * The 7 least significant bits are used to encode the value and the most significant bit indicates whether there's another
 * byte after it for the next part of the number. The least significant group is written first,
 * followed by each of the more significant groups; thus,
 * VarInts are effectively little endian (however, groups are 7 bits, not 8).
 * <p>
 * VarInts are never longer than 5 bytes, and VarLongs are never longer than 10 bytes.
 * Within these limits, unnecessarily long encodings (e.g. 81 00 to encode 1) are allowed.
 */
@Supports({Long.class, long.class})
public class VarLongSerializer implements Serializer<Long> {

    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    @Override
    public void serialize(SerializerContext context, DataVisitor visitor, Long value) {
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
    public Long deserialize(SerializerContext context, DataVisitor visitor) {
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
