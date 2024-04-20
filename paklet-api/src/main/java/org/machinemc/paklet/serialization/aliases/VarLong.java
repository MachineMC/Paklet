package org.machinemc.paklet.serialization.aliases;

import org.machinemc.paklet.serialization.VarLongSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Alias for {@link VarLongSerializer}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
@SerializerAlias(VarLongSerializer.class)
public @interface VarLong {
}
