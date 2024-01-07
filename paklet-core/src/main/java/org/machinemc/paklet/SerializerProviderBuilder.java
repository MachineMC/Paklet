package org.machinemc.paklet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Contract;
import org.machinemc.paklet.serializers.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class SerializerProviderBuilder {

    private final Map<Class<?>, Serializer<?>> instances = new HashMap<>();
    private final Map<Class<?>, Serializer<?>> serializers = new HashMap<>();
    private final Set<SerializationRule> rules = new LinkedHashSet<>();

    public static SerializerProviderBuilder create() {
        return new SerializerProviderBuilder();
    }

    private SerializerProviderBuilder() {
    }

    /**
     * Adds serializer to this provider. Can override already added serializers.
     * <p>
     * Determinate the types using {@link Supports} annotation.
     *
     * @param serializer serializer to add
     * @return this
     */
    public SerializerProviderBuilder add(Serializer<?> serializer) {
        Class<?> clazz = serializer.getClass();
        return clazz.isAnnotationPresent(Supports.class)
                ? add(serializer, clazz.getAnnotation(Supports.class).value())
                : add(serializer, new Class[0]);
    }

    /**
     * Adds serializer to this provider. Can override already added serializers.
     *
     * @param serializer serializer to add
     * @param types types that should be serialized/deserialized with this serializer
     * @return this
     */
    public SerializerProviderBuilder add(Serializer<?> serializer, Class<?>... types) {
        instances.put(serializer.getClass(), serializer);
        for (Class<?> type : types) serializers.put(type, serializer);
        return this;
    }

    /**
     * Adds new rule to this provider, the order is preserved.
     *
     * @param rule rule to add
     * @return this
     */
    public SerializerProviderBuilder addRule(SerializationRule rule) {
        rules.add(rule);
        return this;
    }

    /**
     * Loads default serializers and rules provided by Paklet.
     *
     * @return this
     */
    public SerializerProviderBuilder loadProvided() {
        add(new Serializers.Boolean());
        add(new Serializers.Byte());
        add(new Serializers.Short());
        add(new Serializers.Integer());
        add(new Serializers.Long());
        add(new Serializers.Float());
        add(new Serializers.Double());
        add(new Serializers.Character());
        add(new Serializers.Number());
        add(new Serializers.String());
        add(new Serializers.Collection());
        add(new Serializers.UUID());
        add(new Serializers.Instant());
        add(new Serializers.BitSet());
        add(new Serializers.Enum());
        add(new Serializers.Array());

        addRule(new EnumSerializationRule());
        addRule(new ArraySerializationRule());
        return this;
    }

    /**
     * Loads default serializers marked with {@link DefaultSerializer} files.
     *
     * @return this
     * @see DefaultSerializer
     */
    public SerializerProviderBuilder loadDefaults() {
        return loadDefaults(DefaultSerializerProvider.class.getResourceAsStream("/paklet-packet-data.json"));
    }

    /**
     * Loads serializers from json.
     * <p>
     * Can be used on platforms where it is impossible to get resources as
     * streams the typical way or to load serializers from other json files.
     *
     * @param is json input
     * @return this
     */
    public SerializerProviderBuilder loadDefaults(InputStream is) {
        try (is) {
            JsonObject json = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
            JsonArray defaultSerializers = json.getAsJsonArray("defaultSerializers");
            for (JsonElement element : defaultSerializers) {
                String className = element.getAsString().replace('/', '.');
                Class<?> serializerClass = Class.forName(className);
                Serializer<?> serializer = (Serializer<?>) serializerClass.getConstructor().newInstance();
                add(serializer);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return this;
    }

    /**
     * @return serializer provider of this builder
     */
    @Contract(pure = true)
    public SerializerProvider build() {
        return new DefaultSerializerProvider(instances, serializers, rules);
    }

}
