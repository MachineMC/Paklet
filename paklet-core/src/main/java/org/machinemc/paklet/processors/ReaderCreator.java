package org.machinemc.paklet.processors;

import org.machinemc.paklet.PacketReader;

@FunctionalInterface
public interface ReaderCreator {

    <T> PacketReader<T> create(Class<T> packet);

}
