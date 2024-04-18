package org.machinemc.paklet.serialization.catalogue;


import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.serialization.rule.SerializationRule;

import java.util.Collection;

/**
 * Represents a catalogue that can register elements manually.
 * <p>
 * This makes it possible to register elements that do not have
 * constructor without arguments easily.
 */
public sealed interface DynamicCatalogue {

    /**
     * Dynamic catalogue for serializers.
     */
    non-sealed interface Serializers extends DynamicCatalogue {

        /**
         * Provides serializers of this catalogue.
         * <p>
         * This fully replaces the dynamic resolution of the serializers,
         * so all elements part of the catalogue are expected,
         * even those which would be normally registered automatically.
         *
         * @return serializers of this catalogue
         */
        Collection<Serializer<?>> provideSerializers();

    }

    /**
     * Dynamic catalogue for serialization rules.
     */
    non-sealed interface SerializationRules extends DynamicCatalogue {

        /**
         * Provides serialization rules of this catalogue.
         * <p>
         * This fully replaces the dynamic resolution of the serialization rules,
         * so all elements part of the catalogue are expected,
         * even those which would be normally registered automatically.
         *
         * @return serialization rules of this catalogue
         */
        Collection<SerializationRule> provideSerializationRules();

    }

}
