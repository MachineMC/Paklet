package org.machinemc.paklet.serializers;

import org.jetbrains.annotations.Nullable;
import org.machinemc.paklet.Serializer;

import java.util.function.Function;

/**
 * Used for providing extra rules for serialization providers.
 */
@FunctionalInterface
public interface SerializationRule extends Function<Class<?>, Serializer<?>> {

    /**
     * Finds an alternative serializer for given type or null if there is none.
     *
     * @param clazz class of the object to serialize
     * @return serializer to use
     */
    @Nullable Serializer<?> apply(Class<?> clazz);

}
