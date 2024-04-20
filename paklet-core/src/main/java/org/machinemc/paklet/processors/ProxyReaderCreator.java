package org.machinemc.paklet.processors;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.PacketReader;
import org.machinemc.paklet.serialization.SerializerContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Objects;

public class ProxyReaderCreator implements ReaderCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T> PacketReader<T> create(Class<T> packet) {
        try {
            Constructor<T> constructor = packet.getDeclaredConstructor();
            constructor.setAccessible(true);
            List<Field> fields = ProcessorsUtil.collectSerializableFields(packet);
            fields.forEach(field -> field.setAccessible(true));
            return (PacketReader<T>) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{PacketReader.class}, (proxy, method, args) -> {

                switch (method.getName()) {
                    case "toString" -> {
                        return "ProxyPacketReader";
                    }
                    case "hashCode" -> {
                        return Objects.hashCode(proxy);
                    }
                    case "equals" -> {
                        return Objects.equals(proxy, args[0]);
                    }
                }

                SerializerContext context = (SerializerContext) args[0];
                DataVisitor visitor = (DataVisitor) args[1];

                T instance = constructor.newInstance();
                for (Field field : fields) {
                    Object value = ProcessorsUtil.getValueForField(context, visitor, packet, field.getName());
                    field.set(instance, value);
                }
                return instance;
            });
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
