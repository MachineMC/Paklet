package org.machinemc.paklet.serializers;

import org.jetbrains.annotations.Nullable;
import org.machinemc.paklet.Serializer;

import java.io.Serializable;

/**
 * Serialization rule for classes implementing serializable interface.
 */
public class SerializableSerializationRule implements SerializationRule {

    @Override
    public @Nullable Serializer<?> findSerializer(SerializerProvider provider, Class<?> clazz) {
        return Serializable.class.isAssignableFrom(clazz) ? provider.getOf(Serializers.Serializable.class) : null;
    }

}
