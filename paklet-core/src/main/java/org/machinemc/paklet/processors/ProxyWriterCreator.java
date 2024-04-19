package org.machinemc.paklet.processors;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.PacketWriter;
import org.machinemc.paklet.serialization.SerializerContext;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Objects;

public class ProxyWriterCreator implements WriterCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T> PacketWriter<T> create(Class<T> packet) {
        try {
            List<Field> fields = ProcessorsUtil.collectSerializableFields(packet);
            fields.forEach(field -> field.setAccessible(true));
            return (PacketWriter<T>) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{PacketWriter.class}, (proxy, method, args) -> {

                switch (method.getName()) {
                    case "toString" -> {
                        return "ProxyPacketWriter";
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
                T instance = (T) args[2];

                for (Field field : fields) {
                    Object value = field.get(instance);
                    ProcessorsUtil.setValueForField(context, visitor, packet, field.getName(), value);
                }
                return instance;
            });
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
