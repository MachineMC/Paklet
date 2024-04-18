package org.machinemc.paklet.serialization.catalogue;

import org.machinemc.paklet.serialization.rule.ArraySerializationRule;
import org.machinemc.paklet.serialization.rule.EnumSerializationRule;
import org.machinemc.paklet.serialization.rule.SerializableSerializationRule;
import org.machinemc.paklet.serialization.rule.SerializationRule;

import java.util.Collection;
import java.util.List;

/**
 * Collection of default serialization rules provided by Paklet library.
 */
public class DefaultSerializationRules implements DynamicCatalogue.SerializationRules {

    @Override
    public Collection<SerializationRule> provideSerializationRules() {
        return List.of(
                new ArraySerializationRule(),
                new EnumSerializationRule(),
                new SerializableSerializationRule()
        );
    }

}
