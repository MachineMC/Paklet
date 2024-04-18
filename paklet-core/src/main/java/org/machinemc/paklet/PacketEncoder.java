package org.machinemc.paklet;

import io.netty.buffer.Unpooled;
import org.machinemc.paklet.netty.NettyDataVisitor;
import org.machinemc.paklet.serialization.VarIntSerializer;

import java.util.Objects;

/**
 * Encodes packet data into a visitor.
 * <p>
 * Is used for default {@link PacketFactory} implementation.
 */
public interface PacketEncoder {

    /**
     * The default implementation of packet encoder that encodes the
     * packet prefixed with its ID as var int, followed by the packet data.
     *
     * @return default var int packet encoder
     * @see org.machinemc.paklet.serialization.VarIntSerializer VarInt explanation
     */
    static PacketEncoder varInt() {
        return new VarIntPacketEncoder();
    }

    /**
     * Encodes the packet to the target data visitor.
     *
     * @param target target
     * @param group provided packet group
     * @param encoded encoded packet
     */
    void encode(DataVisitor target, String group, Encoded encoded);

    /**
     * Decodes packet from the source data visitor.
     *
     * @param source source
     * @param group provided packet group
     * @return decoded packet
     */
    Encoded decode(DataVisitor source, String group);

    /**
     * Represents encoded packet.
     *
     * @param packetID packet id
     * @param packetData data of the packet
     */
    record Encoded(int packetID, DataVisitor packetData) {

        public Encoded {
            if (packetID < 0) throw new IllegalArgumentException("Invalid packet ID " + packetData);
            Objects.requireNonNull(packetData, "Packet data can not be null");
        }

    }

}

class VarIntPacketEncoder implements PacketEncoder {

    private final VarIntSerializer serializer = new VarIntSerializer();

    @Override
    public void encode(DataVisitor target, String group, Encoded encoded) {
        serializer.serialize(null, target, encoded.packetID());
        target.write(encoded.packetData());
    }

    @Override
    public Encoded decode(DataVisitor source, String group) {
        int packetID = serializer.deserialize(null, source);
        DataVisitor packetData = new NettyDataVisitor(Unpooled.buffer());
        packetData.write(source);
        return new Encoded(packetID, packetData);
    }

}
