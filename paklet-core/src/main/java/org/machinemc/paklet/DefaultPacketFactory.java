package org.machinemc.paklet;

import org.machinemc.paklet.processors.*;
import org.machinemc.paklet.serializers.SerializerContext;
import org.machinemc.paklet.serializers.SerializerProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class DefaultPacketFactory implements PacketFactory {

    private final Serializer<Integer> idSerializer;
    private final SerializerProvider serializerProvider;

    private final Map<Class<?>, PacketGroup> packet2Group = new ConcurrentHashMap<>();
    private final Map<String, PacketGroup> groups = new ConcurrentHashMap<>();

    DefaultPacketFactory(Serializer<Integer> idSerializer, SerializerProvider serializerProvider) {
        this.idSerializer = idSerializer;
        this.serializerProvider = serializerProvider;
    }

    @Override
    public <T> void addPacket(Class<T> packetClass) {
        ReaderCreator readerCreator;
        WriterCreator writerCreator;

        if (CustomPacket.class.isAssignableFrom(packetClass)) {
            readerCreator = new CustomReaderCreator();
            writerCreator = new CustomWriterCreator();
        }

        else if (ProcessorsUtil.isGeneratedPacketClass(packetClass)) {
            readerCreator = new GeneratedReaderCreator();
            writerCreator = new GeneratedWriterCreator();
        }

        else {
            readerCreator = new ProxyReaderCreator();
            writerCreator = new ProxyWriterCreator();
        }

        addPacket(packetClass, readerCreator.create(packetClass), writerCreator.create(packetClass));
    }

    @Override
    public <T> void addPacket(Class<T> packetClass, PacketReader<T> reader, PacketWriter<T> writer) {
        Packet annotation = packetClass.getAnnotation(Packet.class);
        if (annotation == null) throw new IllegalArgumentException("Class " + packetClass.getName() + " is not a valid packet class");
        addPacket(packetClass, reader, writer, getPacketID(packetClass), annotation.group());
    }

    @Override
    public <T> void addPacket(Class<T> packetClass, PacketReader<T> reader, PacketWriter<T> writer, int packetID, String packetGroup) {
        if (packetClass == null || reader == null || writer == null) throw new NullPointerException();
        if (packetID == Packet.INVALID_PACKET) return; // invalid packets should be ignored
        if (packetID < 0) throw new IllegalArgumentException("Invalid packet ID for packet " + packetClass.getName());
        PacketGroup group = groups.computeIfAbsent(packetGroup, g -> new PacketGroup());
        packet2Group.put(packetClass, group);
        group.addPacket(packetID, packetClass, reader, writer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(String group, DataVisitor visitor) {
        SerializerContext context = new SerializerContext(null, serializerProvider);
        PacketGroup packetGroup = groups.get(group);
        if (packetGroup == null) throw new NullPointerException("There is no " + group + " packet group");
        Class<?> packetClass = packetGroup.getPacket(visitor.read(context, idSerializer));
        return (T) packetGroup.getReader(packetClass).read(context, visitor);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void write(T packet, DataVisitor visitor) {
        SerializerContext context = new SerializerContext(null, serializerProvider);
        Class<?> packetClass = packet.getClass();
        PacketGroup packetGroup = packet2Group.get(packetClass);
        if (packetGroup == null) throw new NullPointerException("Packet " + packetClass.getName() + " is not assigned to any group");
        PacketWriter<T> writer = (PacketWriter<T>) packetGroup.getWriter(packetClass);
        visitor.write(context, idSerializer, packetGroup.getID(packetClass));
        writer.write(context, visitor, packet);
    }

    private int getPacketID(Class<?> packetClass) {
        Packet annotation = packetClass.getAnnotation(Packet.class);
        if (annotation == null) throw new IllegalArgumentException("Class " + packetClass.getName() + " is not a valid packet class");
        if (annotation.value() == Packet.INVALID_PACKET) return Packet.INVALID_PACKET;
        if (annotation.value() == Packet.DYNAMIC_PACKET) {
            Field[] packetIDFields = Arrays.stream(packetClass.getDeclaredFields())
                    .filter(f -> Modifier.isStatic(f.getModifiers()))
                    .filter(f -> f.getType().equals(int.class))
                    .filter(f -> f.isAnnotationPresent(PacketID.class))
                    .toArray(Field[]::new);
            if (packetIDFields.length == 0) throw new IllegalStateException("Class " + packetClass.getName() + " is missing packet ID field");
            if (packetIDFields.length > 1) throw new IllegalStateException("Class " + packetClass.getName() + " has more than one packet ID field");
            try {
                packetIDFields[0].setAccessible(true);
                return (int) packetIDFields[0].get(null);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
        return annotation.value();
    }

    static class PacketGroup {

        private final Map<Integer, Class<?>> id2Packet = new ConcurrentHashMap<>();
        private final Map<Class<?>, Integer> packet2ID = new ConcurrentHashMap<>();

        private final Map<Class<?>, PacketReader<?>> readers = new ConcurrentHashMap<>();
        private final Map<Class<?>, PacketWriter<?>> writers = new ConcurrentHashMap<>();

        public void addPacket(int packetID, Class<?> packetClass, PacketReader<?> reader, PacketWriter<?> writer) {
            if (packet2ID.containsKey(packetClass)) return;
            if (id2Packet.containsKey(packetID))
                throw new IllegalArgumentException("ID " + packetID + " is already used by " + getPacket(packetID).getName());

            id2Packet.put(packetID, packetClass);
            packet2ID.put(packetClass, packetID);
            readers.put(packetClass, reader);
            writers.put(packetClass, writer);
        }

        public Class<?> getPacket(int packetID) {
            Class<?> packetClass = id2Packet.get(packetID);
            if (packetClass == null) throw new NullPointerException();
            return packetClass;
        }

        public int getID(Class<?> packetClass) {
            Integer id = packet2ID.get(packetClass);
            if (id == null) throw new NullPointerException();
            return id;
        }

        public PacketReader<?> getReader(Class<?> packetClass) {
            PacketReader<?> reader = readers.get(packetClass);
            if (reader == null) throw new NullPointerException();
            return reader;
        }

        public PacketWriter<?> getWriter(Class<?> packetClass) {
            PacketWriter<?> writer = writers.get(packetClass);
            if (writer == null) throw new NullPointerException();
            return writer;
        }

    }

}
