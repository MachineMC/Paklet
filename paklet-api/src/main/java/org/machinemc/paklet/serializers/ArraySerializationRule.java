package org.machinemc.paklet.serializers;

import org.jetbrains.annotations.Nullable;
import org.machinemc.paklet.Serializer;

/**
 * Serialization rule for array objects.
 */
public class ArraySerializationRule implements SerializationRule {

    @Override
    public @Nullable Serializer<?> findSerializer(SerializerProvider provider, Class<?> clazz) {
        return clazz.isArray() ? provider.getOf(Serializers.Array.class) : null;
    }

}
