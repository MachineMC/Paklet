package org.machinemc.paklet;

import java.util.function.BiConsumer;

/**
 * Used to quickly write packets to a data visitors.
 *
 * @param <T> packet
 */
@FunctionalInterface
public interface PacketWriter<T /* Packet */> extends BiConsumer<DataVisitor, T> {
}
