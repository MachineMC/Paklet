package org.machinemc.paklet.modifiers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks field of a packet as nullable. By default, serialization fails if
 * a field is a null and is not marked as optional.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
public @interface Optional {
}
