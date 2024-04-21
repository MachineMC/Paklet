package org.machinemc.paklet.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for limiting min and max value of whole numbers.
 * <p>
 * By default, supported by {@link org.machinemc.paklet.serialization.Serializers.Byte},
 * {@link org.machinemc.paklet.serialization.Serializers.Short},
 * {@link org.machinemc.paklet.serialization.Serializers.Integer},
 * {@link org.machinemc.paklet.serialization.Serializers.Long},
 * {@link org.machinemc.paklet.serialization.Serializers.Float},
 * {@link org.machinemc.paklet.serialization.Serializers.Double}, and
 * {@link org.machinemc.paklet.serialization.Serializers.Number}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
public @interface Range {

    /**
     * @return min allowed value
     */
    long min() default Long.MIN_VALUE;

    /**
     * @return max allowed value
     */
    long max() default Long.MAX_VALUE;

    /**
     * @return whether the values are inclusive
     */
    boolean inclusive();

}
