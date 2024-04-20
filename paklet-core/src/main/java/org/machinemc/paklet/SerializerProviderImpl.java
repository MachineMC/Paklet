package org.machinemc.paklet;

import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.paklet.serialization.NoSuchSerializerException;
import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.serialization.Supports;
import org.machinemc.paklet.serialization.catalogue.DynamicCatalogue;
import org.machinemc.paklet.serialization.rule.SerializationRule;
import org.machinemc.paklet.serialization.SerializerProvider;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Default implementation of serializer provider.
 */
public class SerializerProviderImpl implements SerializerProvider {

    // serializer types -> serializers
    private final Map<Class<?>, Serializer<?>> types2Serializers = new ConcurrentHashMap<>();
    // supported types -> serializers
    private final Map<Class<?>, Serializer<?>> supported2Serializers = new ConcurrentHashMap<>();

    private final Set<SerializationRule> rules = new LinkedHashSet<>();

    // map of serializers that are not registered but had been previously
    // resolved by #getOf
    private final Map<Class<?>, Serializer<?>> cachedSerializers = new ConcurrentHashMap<>();

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

        cachedSerializers.remove(serializer.getClass());
    }

    @Override
    public <T extends Serializer<?>> void addSerializer(Class<T> serializerClass) {
        try {
            Constructor<T> constructor = serializerClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            addSerializer((Serializer<?>) constructor.newInstance());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public <Catalogue> void addSerializers(Class<Catalogue> catalogueClass) {
        addSerializers(catalogueClass, catalogueClass::getResourceAsStream);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Catalogue> void addSerializers(Class<Catalogue> catalogueClass, Function<String, InputStream> resourcesAccessor) {
        if (DynamicCatalogue.Serializers.class.isAssignableFrom(catalogueClass)) {
            try {
                Constructor<Catalogue> constructor = catalogueClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                addSerializers((DynamicCatalogue.Serializers) constructor.newInstance());
                return;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        try {
            List<Class<?>> classes = CatalogueUtils.getClassesOfCatalogue(catalogueClass, "serializers", resourcesAccessor);
            for (Class<?> clazz : classes) addSerializer((Class<? extends Serializer<?>>) clazz);
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
            Constructor<T> constructor = ruleClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            addSerializationRule(constructor.newInstance());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public <Catalogue> void addSerializationRules(Class<Catalogue> catalogueClass) {
        addSerializationRules(catalogueClass, catalogueClass::getResourceAsStream);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Catalogue> void addSerializationRules(Class<Catalogue> catalogueClass, Function<String, InputStream> resourcesAccessor) {
        if (DynamicCatalogue.SerializationRules.class.isAssignableFrom(catalogueClass)) {
            try {
                Constructor<Catalogue> constructor = catalogueClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                addSerializationRules((DynamicCatalogue.SerializationRules) constructor.newInstance());
                return;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        try {
            List<Class<?>> classes = CatalogueUtils.getClassesOfCatalogue(catalogueClass, "rules", resourcesAccessor);
            for (Class<?> clazz : classes) addSerializationRule((Class<? extends SerializationRule>) clazz);
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
    public @Unmodifiable Collection<Serializer<?>> getRegisteredSerializers() {
        return Collections.unmodifiableCollection(types2Serializers.values());
    }

    @Override
    public @Unmodifiable Collection<SerializationRule> getRegisteredSerializationRules() {
        return Collections.unmodifiableSet(rules);
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
    @SuppressWarnings("unchecked")
    public <T extends Serializer<?>> T getOf(Class<T> clazz) throws NoSuchSerializerException {
        T serializer;

        serializer = (T) types2Serializers.get(clazz);
        if (serializer != null) return serializer;

        serializer = (T) cachedSerializers.get(clazz);
        if (serializer != null) return serializer;

        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            serializer = constructor.newInstance();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to initiate " + clazz.getName() + " serializer because it has no default constructor");
        }

        cachedSerializers.put(clazz, serializer);
        return serializer;
    }

}
