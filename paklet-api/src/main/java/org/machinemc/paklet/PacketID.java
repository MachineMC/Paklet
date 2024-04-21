package org.machinemc.paklet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate static int fields of packet classes with
 * dynamic packet IDs.
 *
 * @see Packet#DYNAMIC_PACKET
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PacketID {
}
