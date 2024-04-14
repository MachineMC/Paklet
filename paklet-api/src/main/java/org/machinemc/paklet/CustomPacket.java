package org.machinemc.paklet;

import org.machinemc.paklet.serializers.SerializerContext;

/**
 * Used for packets that are serialized manually (not automatically using registered serializers).
 * <p>
 * This can be used for packets that require very specific serialization to be done to optimize
 * the process and keeping the code clean.
 */
public interface CustomPacket {

    /**
     * Used during deserialization of the packet to read the packet data.
     *
     * @param context serialization context
     * @param visitor visitor
     */
    void construct(SerializerContext context, DataVisitor visitor);

    /**
     * Used during serialization of the packet to write the packet data.
     *
     * @param context serialization context
     * @param visitor visitor
     */
    void deconstruct(SerializerContext context, DataVisitor visitor);

}
