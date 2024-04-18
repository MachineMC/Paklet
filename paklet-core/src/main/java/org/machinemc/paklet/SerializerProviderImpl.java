package org.machinemc.paklet;

import org.machinemc.paklet.serialization.NoSuchSerializerException;
import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.serialization.Supports;
import org.machinemc.paklet.serialization.catalogue.DynamicCatalogue;
import org.machinemc.paklet.serialization.rule.SerializationRule;
import org.machinemc.paklet.serialization.SerializerProvider;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of serializer provider.
 */
public class SerializerProviderImpl implements SerializerProvider {

    // serializer types -> serializers
    private final Map<Class<?>, Serializer<?>> types2Serializers = new ConcurrentHashMap<>();
    // supported types -> serializers
    private final Map<Class<?>, Serializer<?>> supported2Serializers = new ConcurrentHashMap<>();

    private final Set<SerializationRule> rules = new LinkedHashSet<>();

    @Override
    public <T> void addSerializer(Serializer<T> serializer) {
        if (types2Serializers.containsKey(serializer.getClass())) return;
        Supports supports = serializer.getClass().getAnnotation(Supports.class);
        Class<?>[] supported = supports != null ? supports.value() : new Class[0];
        for (Class<?> supportedType : supported) {
            if (!supported2Serializers.containsKey(supportedType)) continue;
            throw new IllegalArgumentException("There is already existing serializer for type " + supportedType.getName());
        }

        types2Serializers.put(serializer.getClass(), serializer);
        Arrays.stream(supported).forEach(supportedType -> supported2Serializers.put(supportedType, serializer));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializer<?>> void addSerializer(Class<T> serializerClass) {
        try {
            addSerializer((Serializer<T>) serializerClass.getConstructor().newInstance());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public <Catalogue> void addSerializers(Class<Catalogue> catalogueClass) {
        if (DynamicCatalogue.Serializers.class.isAssignableFrom(catalogueClass)) {
            try {
                addSerializers((DynamicCatalogue.Serializers) catalogueClass.getConstructor().newInstance());
                return;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        try {
            List<Class<?>> classes = CatalogueUtils.getClassesOfCatalogue(catalogueClass, "serializers");
            for (Class<?> clazz : classes) {
                Serializer<?> serializer = (Serializer<?>) clazz.getConstructor().newInstance();
                addSerializer(serializer);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public <Catalogue extends DynamicCatalogue.Serializers> void addSerializers(Catalogue catalogue) {
        catalogue.provideSerializers().forEach(this::addSerializer);
    }

    @Override
    public <T extends Serializer<?>> boolean removeSerializer(Class<T> serializerClass) {
        if (!types2Serializers.containsKey(serializerClass)) return false;
        Supports supports = serializerClass.getAnnotation(Supports.class);
        Class<?>[] supported = supports != null ? supports.value() : new Class[0];
        types2Serializers.remove(serializerClass);
        Arrays.stream(supported).forEach(supported2Serializers::remove);
        return true;
    }

    @Override
    public void addSerializationRule(SerializationRule rule) {
        rules.add(rule);
    }

    @Override
    public <T extends SerializationRule> void addSerializationRule(Class<T> ruleClass) {
        try {
            addSerializationRule(ruleClass.getConstructor().newInstance());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public <Catalogue> void addSerializationRules(Class<Catalogue> catalogueClass) {
        if (DynamicCatalogue.SerializationRules.class.isAssignableFrom(catalogueClass)) {
            try {
                addSerializationRules((DynamicCatalogue.SerializationRules) catalogueClass.getConstructor().newInstance());
                return;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        try {
            List<Class<?>> classes = CatalogueUtils.getClassesOfCatalogue(catalogueClass, "rules");
            for (Class<?> clazz : classes) {
                SerializationRule rule = (SerializationRule) clazz.getConstructor().newInstance();
                addSerializationRule(rule);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public <Catalogue extends DynamicCatalogue.SerializationRules> void addSerializationRules(Catalogue catalogue) {
        catalogue.provideSerializationRules().forEach(this::addSerializationRule);
    }

    @Override
    public <T extends SerializationRule> boolean removeSerializationRule(T rule) {
        return rules.remove(rule);
    }

    @Override
    public <T extends SerializationRule> boolean removeSerializationRule(Class<T> ruleClass) {
        List<SerializationRule> toRemove = rules.stream().filter(r -> ruleClass.isAssignableFrom(r.getClass())).toList();
        if (toRemove.isEmpty()) return false;
        toRemove.forEach(rules::remove);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Serializer<T> getFor(Class<T> type) throws NoSuchSerializerException {
        if (supported2Serializers.containsKey(type))
            return (Serializer<T>) supported2Serializers.get(type);
        for (SerializationRule rule : rules) {
            Serializer<?> serializer = rule.findSerializer(this, type);
            if (serializer != null) return (Serializer<T>) serializer;
        }
        throw new NullPointerException("No serializer found for type " + type.getName());
    }

    @Override
    public <T extends Serializer<?>> T getOf(Class<T> clazz) throws NoSuchSerializerException {
        if (types2Serializers.containsKey(clazz))
            types2Serializers.get(clazz);
        T serializer;
        try {
            serializer = clazz.getConstructor().newInstance();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to initiate " + clazz.getName() + " serializer because it has no default constructor");
        }
        types2Serializers.put(clazz, serializer);
        return serializer;
    }

}
