package org.machinemc.paklet;

import org.jetbrains.annotations.Unmodifiable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
     * @param <PacketType> packet
     *
     * @throws IllegalArgumentException if packet with the same ID and group is already registered
     */
    <PacketType> void addPacket(Class<PacketType> packetClass);

    /**
     * Adds new packet information to this factory.
     *
     * @param packetClass class of the packet
     * @param reader reader for the packet
     * @param writer writer for the packet
     * @param <PacketType> packet
     *
     * @throws IllegalArgumentException if packet with the same ID and group is already registered
     */
    <PacketType> void addPacket(Class<PacketType> packetClass, PacketReader<PacketType> reader, PacketWriter<PacketType> writer);

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
     * @param group group the packet should be registered to
     * @param <PacketType> packet
     *
     * @throws IllegalArgumentException if packet with the same ID and group is already registered
     */
    <PacketType> void addPacket(Class<PacketType> packetClass, PacketReader<PacketType> reader, PacketWriter<PacketType> writer, int packetID, String group);

    /**
     * Adds all packets from a catalogue.
     *
     * @param catalogueClass class of the catalogue
     * @param <Catalogue> catalogue
     *
     * @throws IllegalArgumentException if packet with the same ID and group is already registered
     */
    <Catalogue> void addPackets(Class<Catalogue> catalogueClass);

    /**
     * Adds all packets from a catalogue.
     * <p>
     * This method allows to customize getting the resource input stream. This can be used
     * on platforms such as Bukkit or Spring where the standard way how accessing resources
     * does not work.
     *
     * @param catalogueClass class of the catalogue
     * @param resourcesAccessor function mapping path to resource input stream
     * @param <Catalogue> catalogue
     *
     * @throws IllegalArgumentException if packet with the same ID and group is already registered
     */
    <Catalogue> void addPackets(Class<Catalogue> catalogueClass, Function<String, InputStream> resourcesAccessor);

    /**
     * Removes packet with given type.
     *
     * @param packetClass class of the packet
     * @return array of groups from where the packet has been removed
     * @param <PacketType> packet
     */
    default <PacketType> String[] removePacket(Class<PacketType> packetClass) {
        String[] groups = getPacketGroup(packetClass).orElse(new String[0]);
        List<String> removed = new ArrayList<>();
        for (String group : groups) {
            int id = getPacketID(packetClass, group);
            if (id == -1) continue;
            if (!removePacket(id, group)) continue;
            removed.add(group);
        }
        return removed.toArray(String[]::new);
    }

    /**
     * Removes packet with given ID and group.
     *
     * @param packetID packet id
     * @param group packet group
     * @return whether the packet has been removed successfully
     */
    boolean removePacket(int packetID, String group);

    /**
     * Returns registered packet class from the packet ID and group.
     *
     * @param packetID packet id
     * @return packet class
     * @param <PacketType> packet
     */
    <PacketType> Optional<Class<PacketType>> getPacketClass(int packetID, String group);

    /**
     * Returns ID for given registered packet class.
     *
     * @param packetClass packet class
     * @param group group of the packet
     * @return packet ID of given packet class, or {@code -1} if the class is
     * not registered
     * @param <PacketType> packet
     */
    <PacketType> int getPacketID(Class<PacketType> packetClass, String group);

    /**
     * Returns group for given registered packet class.
     *
     * @param packetClass packet class
     * @return packet class of given packet class
     * @param <PacketType> packet
     */
    <PacketType> Optional<String[]> getPacketGroup(Class<PacketType> packetClass);

    /**
     * Checks whether the given packet class is registered in given group.
     *
     * @param packetClass packet class
     * @param group group
     * @return whether the packet class is registered in the group
     * @param <PacketType> packet
     */
    default <PacketType> boolean isRegistered(Class<PacketType> packetClass, String group) {
        return getPacketID(packetClass, group) != -1;
    }

    /**
     * Checks whether packet with given id and group is registered.
     *
     * @param packetID packet ID
     * @param group packet group
     * @return whether packet with id and group is registered
     */
    default boolean isRegistered(int packetID, String group) {
        return getPacketClass(packetID, group).isPresent();
    }

    /**
     * Returns collection of all currently registered packets.
     *
     * @return collection of registered packets
     */
    @Unmodifiable Collection<Class<?>> getRegisteredPackets();

    /**
     * Creates new packet instance from given group and data (with packet ID).
     * <p>
     * Created instance depends on provided group.
     *
     * @param group group
     * @param visitor visitor with data
     * @return new packet
     * @param <PacketType> packet
     */
    <PacketType> PacketType create(String group, DataVisitor visitor);

    /**
     * Creates new packet instance from given ID, group, and data (without packet ID).
     *
     * @param packetID packet ID
     * @param group group
     * @param visitor visitor with data
     * @return new packet
     * @param <PacketType> packet
     */
    <PacketType> PacketType create(int packetID, String group, DataVisitor visitor);

    /**
     * Writes packet to the provided data visitor.
     *
     * @param packet packet to write
     * @param group group
     * @param visitor visitor
     * @param <PacketType> packet
     */
    <PacketType> void write(PacketType packet, String group, DataVisitor visitor);

}
