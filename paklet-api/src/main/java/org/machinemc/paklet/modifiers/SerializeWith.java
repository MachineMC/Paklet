package org.machinemc.paklet.modifiers;

import org.machinemc.paklet.Serializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify a serializer to use for a packet field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
public @interface SerializeWith {

    /**
     * Class of the serializer to use.
     *
     * @return serializer to use
     */
    Class<? extends Serializer<?>> value();

}
