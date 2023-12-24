package org.machinemc.paklet.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for limiting min and max length of certain elements.
 * <p>
 * By default, supported by {@link org.machinemc.paklet.serializers.Serializers.String},
 * {@link org.machinemc.paklet.serializers.Serializers.Collection},
 * {@link org.machinemc.paklet.serializers.Serializers.BitSet}, and
 * {@link org.machinemc.paklet.serializers.Serializers.Array}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
public @interface Length {

    int min() default 0;

    int max() default Integer.MAX_VALUE;

}
