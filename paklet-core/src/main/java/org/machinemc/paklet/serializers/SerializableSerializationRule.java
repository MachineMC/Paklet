package org.machinemc.paklet.serializers;

import org.jetbrains.annotations.Nullable;
import org.machinemc.paklet.Serializer;

import java.io.Serializable;

public class SerializableSerializationRule implements SerializationRule {

    @Override
    public @Nullable Serializer<?> apply(Class<?> clazz) {
        return Serializable.class.isAssignableFrom(clazz) ? Serializer.context().serializerProvider().getOf(Serializers.Serializable.class) : null;
    }

}
