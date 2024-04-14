package org.machinemc.paklet.serializers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used by serializers to provide what types they support.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Supports {

    /**
     * Array of types supported by the serializer.
     *
     * @return types supported by the serializer
     */
    Class<?>[] value();

}
