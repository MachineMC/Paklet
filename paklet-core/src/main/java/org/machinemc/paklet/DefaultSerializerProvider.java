package org.machinemc.paklet;

import org.machinemc.paklet.serializers.NoSuchSerializerException;
import org.machinemc.paklet.serializers.SerializationRule;
import org.machinemc.paklet.serializers.SerializerProvider;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of serializer provider.
 */
class DefaultSerializerProvider implements SerializerProvider {

    /**
     * serializer types -> serializers
     */
    private final Map<Class<?>, Serializer<?>> instances = new ConcurrentHashMap<>();

    /**
     * supported types -> serializers
     */
    private final Map<Class<?>, Serializer<?>> serializers = new ConcurrentHashMap<>();

    /**
     * supported serialization rules
     */
    private final Set<SerializationRule> rules = new LinkedHashSet<>();

    DefaultSerializerProvider(Map<Class<?>, Serializer<?>> instances, Map<Class<?>, Serializer<?>> serializers, Collection<SerializationRule> rules) {
        this.instances.putAll(instances);
        this.serializers.putAll(serializers);
        this.rules.addAll(rules);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Serializer<T> getFor(Class<T> type) throws NoSuchSerializerException {
        if (serializers.containsKey(type))
            return (Serializer<T>) serializers.get(type);
        for (SerializationRule rule : rules) {
            Serializer<?> serializer = rule.apply(type);
            if (serializer != null) return (Serializer<T>) serializer;
        }
        throw new NullPointerException(STR."No serializer found for type \{type.getName()}");
    }

    @Override
    public <T extends Serializer<?>> T getOf(Class<T> clazz) throws NoSuchSerializerException {
        if (instances.containsKey(clazz))
            instances.get(clazz);
        T serializer;
        try {
            serializer = clazz.getConstructor().newInstance();
        } catch (Exception exception) {
            throw new RuntimeException(STR."Failed to initiate \{clazz.getName()} serializer because it has no default constructor");
        }
        instances.put(clazz, serializer);
        return serializer;
    }

}
