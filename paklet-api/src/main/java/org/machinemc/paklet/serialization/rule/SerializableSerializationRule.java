package org.machinemc.paklet.serialization.rule;

import org.jetbrains.annotations.Nullable;
import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.serialization.SerializerProvider;
import org.machinemc.paklet.serialization.Serializers;
import org.machinemc.paklet.serialization.catalogue.DefaultSerializationRules;

import java.io.Serializable;

/**
 * Serialization rule for classes implementing serializable interface.
 */
@DefaultSerializationRule(DefaultSerializationRules.class)
public class SerializableSerializationRule implements SerializationRule {

    @Override
    public @Nullable Serializer<?> findSerializer(SerializerProvider provider, Class<?> clazz) {
        return Serializable.class.isAssignableFrom(clazz) ? provider.getOf(Serializers.Serializable.class) : null;
    }

}
