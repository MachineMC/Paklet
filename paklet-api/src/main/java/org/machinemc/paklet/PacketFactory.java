package org.machinemc.paklet;

/**
 * Factory managing packet writers and readers for serializing and deserializing packets.
 */
public interface PacketFactory {

    /**
     * Adds new packet information to this factory.
     * <p>
     * Automatically generates readers and writers.
     *
     * @param packetClass class of the packet
     * @param <T> packet
     */
    <T /* Packet */> void addPacket(Class<T> packetClass);

    /**
     * Adds new packet information to this factory.
     *
     * @param packetClass class of the packet
     * @param reader reader for the packet
     * @param writer writer for the packet
     * @param <T> packet
     */
    <T /* Packet */> void addPacket(Class<T> packetClass,
                                    PacketReader<T> reader,
                                    PacketWriter<T> writer);

    /**
     * Adds new packet information to this factory.
     * <p>
     * This method does not require the packet class to be annotated with
     * {@link Packet} annotation.
     *
     * @param packetClass class of the packet
     * @param reader reader for the packet
     * @param writer writer for the packet
     * @param packetID packet ID used to register this packet
     * @param packetGroup group name of this packet
     * @param <T> packet
     */
    <T /* Packet */> void addPacket(Class<T> packetClass,
                                    PacketReader<T> reader,
                                    PacketWriter<T> writer,
                                    int packetID,
                                    String packetGroup);

    /**
     * Creates new packet instance from given data.
     * <p>
     * Created instance depends on provided group.
     *
     * @param group group
     * @param visitor visitor with data
     * @return new packet
     * @param <T> packet
     */
    <T /* Packet */> T create(String group, DataVisitor visitor);

    /**
     * Writes packet to the provided data visitor.
     *
     * @param packet packet to write
     * @param visitor visitor
     * @param <T> packet
     */
    <T /* Packet */> void write(T packet, DataVisitor visitor);

}
