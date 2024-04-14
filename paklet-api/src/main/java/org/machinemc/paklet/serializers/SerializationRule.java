package org.machinemc.paklet.serializers;

import org.jetbrains.annotations.Nullable;
import org.machinemc.paklet.Serializer;

/**
 * Used for providing extra rules for serialization providers.
 */
@FunctionalInterface
public interface SerializationRule {

    /**
     * Finds an alternative serializer for given type or null if there is none.
     *
     * @param provider serializer provider
     * @param clazz class of the object to serialize
     * @return serializer to use
     */
    @Nullable Serializer<?> findSerializer(SerializerProvider provider, Class<?> clazz);

}
