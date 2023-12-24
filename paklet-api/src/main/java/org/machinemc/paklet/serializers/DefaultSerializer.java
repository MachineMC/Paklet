package org.machinemc.paklet.serializers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark default serializers for packet fields.
 * <p>
 * Classes marked with this annotation need to have constructor with no arguments.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultSerializer {
}
