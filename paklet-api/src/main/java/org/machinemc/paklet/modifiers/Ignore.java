package org.machinemc.paklet.modifiers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks field of packet as a field that should not be serialized
 * and ignored instead.
 * <p>
 * Transient field modifier can be used instead.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
public @interface Ignore {
}
