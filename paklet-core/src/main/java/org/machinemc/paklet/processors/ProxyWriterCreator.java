package org.machinemc.paklet.processors;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.PacketWriter;
import org.machinemc.paklet.modifiers.Ignore;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

public class ProxyWriterCreator implements WriterCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T> PacketWriter<T> create(Class<T> packet) {
        try {
            List<Field> fields = Arrays.stream(packet.getDeclaredFields())
                    .filter(field -> !Modifier.isTransient(field.getModifiers()))
                    .filter(field -> !field.isAnnotationPresent(Ignore.class))
                    .toList();
            fields.forEach(field -> field.setAccessible(true));
            return (PacketWriter<T>) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{PacketWriter.class}, (_, _, args) -> {
                DataVisitor visitor = (DataVisitor) args[0];
                T instance = (T) args[1];
                for (Field field : fields) {
                    Object value = ProcessorsUtil.getValueForField(visitor, packet, field.getName());
                    field.set(instance, value);
                }
                return instance;
            });
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
