package org.machinemc.paklet.metadata;

import org.machinemc.paklet.Serializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify a serializer to use for the inner elements of an array.
 * <p>
 * If not specified the default one is used.
 * @see org.machinemc.paklet.serializers.Serializers.Array
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
public @interface SerializeElementsWith {

    Class<? extends Serializer<?>> value();

}
