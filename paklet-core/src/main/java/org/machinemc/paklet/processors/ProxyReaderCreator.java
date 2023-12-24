package org.machinemc.paklet.processors;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.PacketReader;
import org.machinemc.paklet.modifiers.Ignore;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

public class ProxyReaderCreator implements ReaderCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T> PacketReader<T> create(Class<T> packet) {
        try {
            Constructor<T> constructor = packet.getConstructor();
            List<Field> fields = Arrays.stream(packet.getDeclaredFields())
                    .filter(field -> !Modifier.isTransient(field.getModifiers()))
                    .filter(field -> !field.isAnnotationPresent(Ignore.class))
                    .toList();
            fields.forEach(field -> field.setAccessible(true));
            return (PacketReader<T>) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{PacketReader.class}, (_, _, args) -> {
                DataVisitor visitor = (DataVisitor) args[0];
                T instance = constructor.newInstance();
                for (Field field : fields) {
                    Object value = field.get(instance);
                    ProcessorsUtil.setValueForField(visitor, packet, field.getName(), value);
                }
                return instance;
            });
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
