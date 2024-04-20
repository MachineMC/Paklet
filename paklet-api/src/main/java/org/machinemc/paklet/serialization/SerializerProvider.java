package org.machinemc.paklet.serialization;

import org.machinemc.paklet.serialization.catalogue.DynamicCatalogue;
import org.machinemc.paklet.serialization.rule.SerializationRule;

import java.io.InputStream;
import java.util.function.Function;

/**
 * Provider of serializers used in current context.
 */
public interface SerializerProvider {

    /**
     * Adds new serializer.
     *
     * @param serializer serializer to register
     * @param <T> type
     *
     * @throws IllegalArgumentException if serializer for same type already exists
     */
    <T> void addSerializer(Serializer<T> serializer);

    /**
     * Adds new serializer.
     * <p>
     * The serializer has to have constructor with no arguments.
     *
     * @param serializerClass class of the serializer
     * @param <T> type
     *
     * @throws IllegalArgumentException if serializer for same type already exists
     */
    <T extends Serializer<?>> void addSerializer(Class<T> serializerClass);

    /**
     * Adds all serializers from a catalogue.
     *
     * @param catalogueClass class of the catalogue
     * @param <Catalogue> catalogue
     *
     * @throws IllegalArgumentException if serializer for same type already exists
     */
    <Catalogue> void addSerializers(Class<Catalogue> catalogueClass);

    /**
     * Adds all serializers from a catalogue.
     * <p>
     * This method allows to customize getting the resource input stream. This can be used
     * on platforms such as Bukkit or Spring where the standard way how accessing resources
     * does not work.
     *
     * @param catalogueClass class of the catalogue
     * @param resourcesAccessor function mapping path to resource input stream
     * @param <Catalogue> catalogue
     *
     * @throws IllegalArgumentException if serializer for same type already exists
     */
    <Catalogue> void addSerializers(Class<Catalogue> catalogueClass, Function<String, InputStream> resourcesAccessor);

    /**
     * Adds all serializers from dynamic catalogue.
     *
     * @param catalogue catalogue
     * @param <Catalogue> catalogue
     *
     * @throws IllegalArgumentException if serializer for same type already exists
     */
    <Catalogue extends DynamicCatalogue.Serializers> void addSerializers(Catalogue catalogue);

    /**
     * Removes serializer.
     *
     * @param serializerClass class of the serializer
     * @param <T> serializer
     */
    <T extends Serializer<?>> boolean removeSerializer(Class<T> serializerClass);

    /**
     * Adds new serialization rule.
     *
     * @param rule serialization rule to register
     */
    void addSerializationRule(SerializationRule rule);

    /**
     * Adds new serialization rule.
     * <p>
     * The serialization rule has to have constructor with no arguments.
     *
     * @param ruleClass class of the serialization rule
     * @param <T> type
     */
    <T extends SerializationRule> void addSerializationRule(Class<T> ruleClass);

    /**
     * Adds all serialization rules from a catalogue.
     *
     * @param catalogueClass class of the catalogue
     * @param <Catalogue> catalogue
     */
    <Catalogue> void addSerializationRules(Class<Catalogue> catalogueClass);

    /**
     * Adds all serialization rules from a catalogue.
     * <p>
     * This method allows to customize getting the resource input stream. This can be used
     * on platforms such as Bukkit or Spring where the standard way how accessing resources
     * does not work.
     *
     * @param catalogueClass class of the catalogue
     * @param resourcesAccessor function mapping path to resource input stream
     * @param <Catalogue> catalogue
     */
    <Catalogue> void addSerializationRules(Class<Catalogue> catalogueClass, Function<String, InputStream> resourcesAccessor);

    /**
     * Adds all serialization rules from dynamic catalogue.
     *
     * @param catalogue catalogue
     * @param <Catalogue> catalogue
     */
    <Catalogue extends DynamicCatalogue.SerializationRules> void addSerializationRules(Catalogue catalogue);

    /**
     * Removes serialization rule.
     *
     * @param rule rule
     * @param <T> serializer
     * @return whether the rule was removed
     */
    <T extends SerializationRule> boolean removeSerializationRule(T rule);

    /**
     * Removes serialization rule.
     *
     * @param ruleClass class of the serialization rule
     * @param <T> serializer
     * @return whether the rule was removed
     */
    <T extends SerializationRule> boolean removeSerializationRule(Class<T> ruleClass);

    /**
     * Returns default serializer for given type.
     *
     * @param type type to get the serializer for
     * @return serializer for given type
     * @param <T> type
     * @throws NoSuchSerializerException if there is no serializer for given type
     */
    <T> Serializer<T> getFor(Class<T> type) throws NoSuchSerializerException;

    /**
     * Returns registered serializer of given class.
     *
     * @param serializerClass class of the serializer
     * @return serializer of given class
     * @param <T> serializer
     * @throws NoSuchSerializerException if there is no serializer of given type
     */
    <T extends Serializer<?>> T getOf(Class<T> serializerClass) throws NoSuchSerializerException;

}
