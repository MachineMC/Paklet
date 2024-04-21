package org.machinemc.paklet.serialization.aliases;

import org.machinemc.paklet.serialization.VarIntSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Alias for {@link VarIntSerializer}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
@SerializerAlias(VarIntSerializer.class)
public @interface VarInt {
}
