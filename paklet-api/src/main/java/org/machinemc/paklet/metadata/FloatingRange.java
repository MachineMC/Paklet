package org.machinemc.paklet.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for limiting min and max value of floating point numbers.
 * <p>
 * By default, supported by {@link org.machinemc.paklet.serialization.Serializers.Float},
 * {@link org.machinemc.paklet.serialization.Serializers.Double}, and
 * {@link org.machinemc.paklet.serialization.Serializers.Number}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
public @interface FloatingRange {

    /**
     * @return min allowed value
     */
    double min() default Double.MIN_VALUE;

    /**
     * @return max allowed value
     */
    double max() default Double.MAX_VALUE;

    /**
     * @return whether the values are inclusive
     */
    boolean inclusive();

}
