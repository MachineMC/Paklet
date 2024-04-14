package org.machinemc.paklet;

import org.machinemc.paklet.serializers.SerializerContext;

/**
 * Used internally to quickly read packets from data visitors.
 *
 * @param <T> packet
 */
@FunctionalInterface
public interface PacketReader<T /* Packet */> {

    /**
     * Reads next packet from a data visitor.
     *
     * @param context context
     * @param dataVisitor data visitor
     * @return packet
     */
    T read(SerializerContext context, DataVisitor dataVisitor);

}
