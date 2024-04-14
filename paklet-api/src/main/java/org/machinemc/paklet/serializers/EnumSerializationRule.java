package org.machinemc.paklet.serializers;

import org.jetbrains.annotations.Nullable;
import org.machinemc.paklet.Serializer;

/**
 * Serialization rule for enums.
 */
public class EnumSerializationRule implements SerializationRule {

    @Override
    public @Nullable Serializer<?> findSerializer(SerializerProvider provider, Class<?> clazz) {
        return clazz.isEnum() ? provider.getOf(Serializers.Enum.class) : null;
    }

}
