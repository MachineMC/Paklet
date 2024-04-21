package org.machinemc.paklet;

import org.machinemc.paklet.serialization.SerializerContext;

/**
 * Used internally to quickly read packets from data visitors.
 *
 * @param <PacketType> packet
 */
@FunctionalInterface
public interface PacketReader<PacketType> {

    /**
     * Reads next packet from a data visitor.
     *
     * @param context context
     * @param dataVisitor data visitor
     * @return packet
     */
    PacketType read(SerializerContext context, DataVisitor dataVisitor);

}
