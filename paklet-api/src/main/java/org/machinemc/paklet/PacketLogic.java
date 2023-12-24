package org.machinemc.paklet;

/**
 * Used for packets that are serialized manually (not automatically using registered serializers).
 * <p>
 * This can be used for packets that require very specific serialization to be done to optimize
 * the process and keeping the code clean.
 */
public interface PacketLogic {

    /**
     * Used during deserialization of the packet to read the packet data.
     * <p>
     * Is called only within serializer context; {@link Serializer#context()} can be used.
     *
     * @param visitor visitor
     */
    void construct(DataVisitor visitor);

    /**
     * Used during serialization of the packet to write the packet data.
     * <p>
     * Is called only within serializer context; {@link Serializer#context()} can be used.
     *
     * @param visitor visitor
     */
    void deconstruct(DataVisitor visitor);

}
