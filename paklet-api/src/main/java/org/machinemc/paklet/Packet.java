package org.machinemc.paklet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks class as serializable packet that can be transferred
 * between server and client.
 * <p>
 * All fields (except for transient or those marked as optional) are
 * serialized with either default or specified serializers.
 * <p>
 * Each packet needs to have its own ID and group. In each group there can be
 * only a single packet with the same ID.
 *
 * @see org.machinemc.paklet.modifiers
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Packet {

    /**
     * Can be used as packet ID for packets which registration should be
     * ignored by {@link PacketFactory}.
     * <p>
     * This can be used for cases where under some condition the packet should
     * not be able to register.
     */
    int INVALID_PACKET = -1;

    /**
     * Can be used as packet ID for packets that require dynamic IDs.
     * <p>
     * As ID provider, static int field inside the packet class
     * annotated with {@link PacketID} needs to be used.
     */
    int DYNAMIC_PACKET = -2;

    /**
     * Represents a default packet group.
     */
    String DEFAULT = "default";

    /**
     * Unique ID of the packet in the packet group.
     * <p>
     * Only positive numbers are expected, negative numbers are reserved for special behaviour.
     *
     * @return ID of the packet
     */
    int id();

    /**
     * Represents a group of the packet.
     * <p>
     * Packets can be split to multiple groups, multiple packets in different groups can have same packet IDs.
     * <p>
     * Compare to packet IDs, packets do not store information about their groups when
     * they are serialized using {@link PacketFactory}.
     *
     * @return group of the packet
     */
    String group() default DEFAULT;

    /**
     * Specifies class that is used as catalogue (identifier) for the packet.
     * <p>
     * All packets within the same catalogue can be easily and automatically registered.
     *
     * @return catalogue source of the packet
     */
    Class<?> catalogue();

}
