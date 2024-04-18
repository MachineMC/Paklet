package org.machinemc.paklet.serialization.rule;

import org.jetbrains.annotations.Nullable;
import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.serialization.SerializerProvider;
import org.machinemc.paklet.serialization.Serializers;
import org.machinemc.paklet.serialization.catalogue.DefaultSerializationRules;

/**
 * Serialization rule for array objects.
 */
@DefaultSerializationRule(DefaultSerializationRules.class)
public class ArraySerializationRule implements SerializationRule {

    @Override
    public @Nullable Serializer<?> findSerializer(SerializerProvider provider, Class<?> clazz) {
        return clazz.isArray() ? provider.getOf(Serializers.Array.class) : null;
    }

}
