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
 * serialized with either default or provided serializers.
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
    int value();

    /**
     * @return group of the packet
     */
    String group() default DEFAULT;

}
