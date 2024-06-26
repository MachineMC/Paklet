package org.machinemc.paklet.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for limiting min and max length of certain elements.
 * <p>
 * By default, supported by {@link org.machinemc.paklet.serialization.Serializers.String},
 * {@link org.machinemc.paklet.serialization.Serializers.Collection},
 * {@link org.machinemc.paklet.serialization.Serializers.Map},
 * {@link org.machinemc.paklet.serialization.Serializers.BitSet}, and
 * {@link org.machinemc.paklet.serialization.Serializers.Array}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
public @interface Length {

    /**
     * @return minimal allowed length
     */
    int min() default 0;

    /**
     * @return maximal allowed length
     */
    int max() default Integer.MAX_VALUE;

}
