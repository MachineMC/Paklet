package org.machinemc.paklet.processors;

import org.machinemc.paklet.PacketReader;

/**
 * Creates reader implementation for given class.
 */
@FunctionalInterface
public interface ReaderCreator {

    /**
     * Creates reader implementation for given class.
     *
     * @param packet packet class
     * @return reader for given packet class
     * @param <T> packet type
     */
    <T> PacketReader<T> create(Class<T> packet);

}
