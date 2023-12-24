package org.machinemc.paklet;

import java.util.function.Function;

/**
 * Used internally to quickly read packets from data visitors.
 *
 * @param <T> packet
 */
@FunctionalInterface
public interface PacketReader<T /* Packet */> extends Function<DataVisitor, T> {
}
