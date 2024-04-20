package org.machinemc.paklet.processors;

import org.machinemc.paklet.PacketWriter;

/**
 * Creates writer implementation for given class.
 */
@FunctionalInterface
public interface WriterCreator {

    /**
     * Creates writer implementation for given class.
     *
     * @param packet packet class
     * @return writer for given packet class
     * @param <T> packet type
     */
    <T> PacketWriter<T> create(Class<T> packet);

}
