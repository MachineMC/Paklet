package org.machinemc.paklet;

import org.machinemc.paklet.serializers.SerializerContext;

/**
 * Used to quickly write packets to a data visitors.
 *
 * @param <T> packet
 */
@FunctionalInterface
public interface PacketWriter<T /* Packet */> {

    /**
     * Writes next packet from to the data visitor.
     *
     * @param context context
     * @param dataVisitor data visitor
     * @param packet packet
     */
    void write(SerializerContext context, DataVisitor dataVisitor, T packet);

}
