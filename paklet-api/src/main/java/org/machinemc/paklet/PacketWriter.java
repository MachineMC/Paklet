package org.machinemc.paklet;

import org.machinemc.paklet.serialization.SerializerContext;

/**
 * Used to quickly write packets to a data visitors.
 *
 * @param <Packet> packet
 */
@FunctionalInterface
public interface PacketWriter<Packet> {

    /**
     * Writes next packet from to the data visitor.
     *
     * @param context context
     * @param dataVisitor data visitor
     * @param packet packet
     */
    void write(SerializerContext context, DataVisitor dataVisitor, Packet packet);

}
