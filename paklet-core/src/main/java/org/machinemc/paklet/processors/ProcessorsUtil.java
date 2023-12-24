package org.machinemc.paklet.processors;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.Serializer;
import org.machinemc.paklet.modifiers.Ignore;
import org.machinemc.paklet.serializers.SerializerContext;

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

    private static final Map<Class<?>, Map<String, AnnotatedType>> CACHED = new ConcurrentHashMap<>();

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
                Method get = packet.getDeclaredMethod(STR."$GET_\{field.getName()}");
                if (!Modifier.isPublic(get.getModifiers())) return false;
                Method set = packet.getDeclaredMethod(STR."$SET_\{field.getName()}", field.getType());
                if (!Modifier.isPublic(set.getModifiers())) return false;
            } catch (Exception exception) {
                return false;
            }
        }
        return true;
    }

    public static Object getValueForField(DataVisitor visitor, Class<?> packet, String name) throws Exception {
        SerializerContext context = createContextForField(packet, name);
        return ScopedValue.callWhere(Serializer.CONTEXT, context, () -> context.serializeWith().deserialize(visitor.readOnly()));
    }

    @SuppressWarnings("unchecked")
    public static void setValueForField(DataVisitor visitor, Class<?> packet, String name, Object value) throws Exception {
        SerializerContext context = createContextForField(packet, name);
        ScopedValue.callWhere(Serializer.CONTEXT, context, () -> {
            Serializer<Object> serializer = (Serializer<Object>) context.serializeWith();
            serializer.serialize(visitor.writeOnly(), value);
            return null;
        });
    }

    public static SerializerContext createContextForField(Class<?> packet, String name) {
        AnnotatedType type;
        CACHED.putIfAbsent(packet, new ConcurrentHashMap<>());
        Map<String, AnnotatedType> types = CACHED.get(packet);
        if (!types.containsKey(name)) {
            try {
                type = packet.getDeclaredField(name).getAnnotatedType();
            } catch (Exception exception) { throw new RuntimeException(exception); }
            types.put(name, type);
        } else {
            type = types.get(name);
        }
        return SerializerContext.withType(type, Serializer.context());
    }

}
