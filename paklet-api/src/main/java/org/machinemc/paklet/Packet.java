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

    String DEFAULT = "default";

    /**
     * @return ID of the packet
     */
    int value();

    /**
     * @return group of the packet
     */
    String group() default DEFAULT;



}
