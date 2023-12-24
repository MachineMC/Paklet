package org.machinemc.paklet.serializers;

import org.jetbrains.annotations.Nullable;
import org.machinemc.paklet.Serializer;

public class EnumSerializationRule implements SerializationRule {

    @Override
    public @Nullable Serializer<?> apply(Class<?> clazz) {
        return clazz.isEnum() ? Serializer.context().serializerProvider().getOf(Serializers.Enum.class) : null;
    }

}
