package org.machinemc.paklet.modifiers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks field of a packet as nullable. By default, serialization fails if
 * a field is null and is not marked as optional.
 * <p>
 * If the field is present during the serialization, the whole value is prefixed by 1, else
 * is serialized as 0.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
public @interface Optional {
}
