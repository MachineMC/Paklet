package org.machinemc.paklet.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify that collection should not be prefixed with its length and instead
 * length specified with {@link FixedLength} should be used.
 * <p>
 * By default, supported by {@link org.machinemc.paklet.serialization.Serializers.Collection},
 * {@link org.machinemc.paklet.serialization.Serializers.Map}, and
 * {@link org.machinemc.paklet.serialization.Serializers.Array}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
public @interface DoNotPrefix {
}
