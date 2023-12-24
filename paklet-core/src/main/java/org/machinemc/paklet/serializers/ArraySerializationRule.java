package org.machinemc.paklet.serializers;

import org.jetbrains.annotations.Nullable;
import org.machinemc.paklet.Serializer;

public class ArraySerializationRule implements SerializationRule {

    @Override
    public @Nullable Serializer<?> apply(Class<?> clazz) {
        return clazz.isArray() ? Serializer.context().serializerProvider().getOf(Serializers.Array.class) : null;
    }

}
