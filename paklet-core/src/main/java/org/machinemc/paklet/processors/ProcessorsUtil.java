package org.machinemc.paklet.processors;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.modifiers.Ignore;
import org.machinemc.paklet.serialization.SerializerContext;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utils used by packet processors.
 */
public final class ProcessorsUtil {

    private ProcessorsUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks whether the provided packet class is modified by the
     * Paklet Gradle plugin.
     *
     * @param packet packet class to check
     * @return whether the provided packet class is modified
     */
    public static boolean isGeneratedPacketClass(Class<?> packet) {
        List<Field> fields = Arrays.stream(packet.getDeclaredFields())
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .filter(field -> !field.isAnnotationPresent(Ignore.class))
                .toList();

        for (Field field : fields) {
            try {
                Method get = packet.getDeclaredMethod("$GET_" + field.getName());
                if (!Modifier.isPublic(get.getModifiers())) return false;
                Method set = packet.getDeclaredMethod("$SET_" + field.getName(), field.getType());
                if (!Modifier.isPublic(set.getModifiers())) return false;
            } catch (Exception exception) {
                return false;
            }
        }
        return true;
    }

    public static List<Field> collectSerializableFields(Class<?> packet) {
        return Arrays.stream(packet.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .filter(field -> !field.isAnnotationPresent(Ignore.class))
                .toList();
    }

    public static Object getValueForField(SerializerContext context, DataVisitor visitor, Class<?> packet, String name) {
        context = createContextForField(context, packet, name);
        return context.serializeWith().deserialize(context, visitor.readOnly());
    }

    @SuppressWarnings("unchecked")
    public static void setValueForField(SerializerContext context, DataVisitor visitor, Class<?> packet, String name, Object value) {
        context = createContextForField(context, packet, name);
        Serializer<Object> serializer = (Serializer<Object>) context.serializeWith();
        serializer.serialize(context, visitor.writeOnly(), value);
    }

    private static final Map<Class<?>, Map<String, AnnotatedType>> CACHED = new ConcurrentHashMap<>();

    public static SerializerContext createContextForField(SerializerContext context, Class<?> packet, String name) {
        AnnotatedType type;
        Map<String, AnnotatedType> types = CACHED.computeIfAbsent(packet, c -> new ConcurrentHashMap<>());
        if (!types.containsKey(name)) {
            try {
                type = packet.getDeclaredField(name).getAnnotatedType();
            } catch (Exception exception) { throw new RuntimeException(exception); }
            types.put(name, type);
        } else {
            type = types.get(name);
        }
        return context.withType(type);
    }

}
