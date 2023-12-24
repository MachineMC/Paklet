package org.machinemc.paklet.processors;

import org.machinemc.paklet.PacketWriter;

@FunctionalInterface
public interface WriterCreator {

    <T> PacketWriter<T> create(Class<T> packet);

}
