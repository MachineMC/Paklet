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

    /**
     * Returns all fields expected to serialize.
     *
     * @param packet packet class
     * @return all its serializable fields
     */
    public static List<Field> collectSerializableFields(Class<?> packet) {
        return Arrays.stream(packet.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .filter(field -> !field.isAnnotationPresent(Ignore.class))
                .toList();
    }

    /**
     * Reads value for given field from given data visitor and returns that value.
     *
     * @param context serialization context
     * @param visitor source visitor
     * @param packet packet type
     * @param field name of the field
     * @return deserialized field value
     */
    public static Object getValueForField(SerializerContext context, DataVisitor visitor, Class<?> packet, String field) {
        context = createContextForField(context, packet, field);
        return context.serializeWith().deserialize(context, visitor.readOnly());
    }

    /**
     * Writes value of given field to given data visitor.
     *
     * @param context serialization context
     * @param visitor target visitor
     * @param packet packet type
     * @param field name of the field
     * @param value value to serialize
     */
    public static void setValueForField(SerializerContext context, DataVisitor visitor, Class<?> packet, String field, Object value) {
        context = createContextForField(context, packet, field);
        Serializer<Object> serializer = context.serializeWith();
        serializer.serialize(context, visitor.writeOnly(), value);
    }

    // cached annotated types, this speed up serialization up to 8 times
    private static final Map<Class<?>, Map<String, AnnotatedType>> CACHED = new ConcurrentHashMap<>();

    /**
     * Creates serialization context for given class and its field.
     *
     * @param context serialization context
     * @param packet packet type
     * @param field name of the field
     * @return serialization context for the field
     */
    public static SerializerContext createContextForField(SerializerContext context, Class<?> packet, String field) {
        AnnotatedType type;
        Map<String, AnnotatedType> types = CACHED.computeIfAbsent(packet, c -> new ConcurrentHashMap<>());
        if (!types.containsKey(field)) {
            try {
                type = packet.getDeclaredField(field).getAnnotatedType();
            } catch (Exception exception) { throw new RuntimeException(exception); }
            types.put(field, type);
        } else {
            type = types.get(field);
        }
        return context.withType(type);
    }

}
