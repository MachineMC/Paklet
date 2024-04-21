package org.machinemc.paklet.serialization.aliases;

import org.machinemc.paklet.serialization.Serializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark annotations that are used as an alias for a serializer.
 * <p>
 * This can be used as a shortcut for {@link org.machinemc.paklet.modifiers.SerializeWith} modifier.
 * <p>
 * Goal of this annotation is to provide more expressive option to specify serializers used
 * for different packet fields.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface SerializerAlias {

    /**
     * The serializer of which this annotation is an alias.
     *
     * @return target serializer
     */
    Class<? extends Serializer<?>> value();

}
