package org.machinemc.paklet.serialization.catalogue;

import org.machinemc.paklet.serialization.Serializer;

import java.util.Collection;
import java.util.List;

/**
 * Collection of default serializers provided by Paklet library.
 */
public class DefaultSerializers implements DynamicCatalogue.Serializers {

    @Override
    public Collection<Serializer<?>> provideSerializers() {
        return List.of(
                new org.machinemc.paklet.serialization.Serializers.Boolean(),
                new org.machinemc.paklet.serialization.Serializers.Byte(),
                new org.machinemc.paklet.serialization.Serializers.Short(),
                new org.machinemc.paklet.serialization.Serializers.Integer(),
                new org.machinemc.paklet.serialization.Serializers.Long(),
                new org.machinemc.paklet.serialization.Serializers.Float(),
                new org.machinemc.paklet.serialization.Serializers.Double(),
                new org.machinemc.paklet.serialization.Serializers.Character(),
                new org.machinemc.paklet.serialization.Serializers.Number(),
                new org.machinemc.paklet.serialization.Serializers.String(),
                new org.machinemc.paklet.serialization.Serializers.Collection(),
                new org.machinemc.paklet.serialization.Serializers.UUID(),
                new org.machinemc.paklet.serialization.Serializers.Instant(),
                new org.machinemc.paklet.serialization.Serializers.BitSet(),
                new org.machinemc.paklet.serialization.Serializers.Enum(),
                new org.machinemc.paklet.serialization.Serializers.Array(),
                new org.machinemc.paklet.serialization.Serializers.Serializable()
        );
    }

}
