package org.machinemc.paklet.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify default serializers for different types.
 * <p>
 * Classes marked with this annotation need to have constructor with no arguments and
 * can be registered automatically from its catalogue.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultSerializer {

    /**
     * Specifies class that is used as catalogue (identifier) for the serializer.
     * <p>
     * All serializers within the same catalogue can be easily and automatically registered.
     *
     * @return catalogue source of the serializer
     */
    Class<?> value();

}
