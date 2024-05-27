package org.machinemc.paklet.metadata;

import org.machinemc.paklet.serialization.Serializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify a serializer to use for prefixing a collection length.
 * <p>
 * By default, supported by {@link org.machinemc.paklet.serialization.Serializers.Collection},
 * {@link org.machinemc.paklet.serialization.Serializers.Map}, and
 * {@link org.machinemc.paklet.serialization.Serializers.Array}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
public @interface LengthUsing {

    /**
     * Class of the serializer to use.
     *
     * @return serializer to use
     */
    Class<? extends Serializer<?>> value();

}
