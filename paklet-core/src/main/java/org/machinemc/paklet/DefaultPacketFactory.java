package org.machinemc.paklet;

import org.machinemc.paklet.processors.*;
import org.machinemc.paklet.serializers.SerializerContext;
import org.machinemc.paklet.serializers.SerializerProvider;

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

        if (PacketLogic.class.isAssignableFrom(packetClass)) {
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
        if (annotation == null) throw new IllegalArgumentException(STR."Class \{packetClass.getName()} is not a valid packet class");
        if (reader == null || writer == null) throw new NullPointerException();

        PacketGroup group = groups.computeIfAbsent(annotation.group(), _ -> new PacketGroup());

        packet2Group.put(packetClass, group);

        group.addPacket(annotation.value(), packetClass, reader, writer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(String group, DataVisitor visitor) {
        try {
            return ScopedValue.callWhere(Serializer.CONTEXT, new SerializerContext(null, serializerProvider), () -> {
                PacketGroup packetGroup = groups.get(group);
                if (packetGroup == null) throw new NullPointerException(STR."There is no \{group} packet group");
                Class<?> packetClass = packetGroup.getPacket(visitor.read(idSerializer));
                return (T) packetGroup.getReader(packetClass).apply(visitor);
            });
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void write(T packet, DataVisitor visitor) {
        try {
            ScopedValue.callWhere(Serializer.CONTEXT, new SerializerContext(null, serializerProvider), () -> {
                Class<?> packetClass = packet.getClass();
                PacketGroup packetGroup = packet2Group.get(packetClass);
                if (packetGroup == null) throw new NullPointerException(STR."Packet \{packetClass.getName()} is not assigned to any group");
                PacketWriter<T> writer = (PacketWriter<T>) packetGroup.getWriter(packetClass);
                visitor.write(idSerializer, packetGroup.getID(packetClass));
                writer.accept(visitor, packet);
                return null;
            });
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    static class PacketGroup {

        private final Map<Integer, Class<?>> id2Packet = new ConcurrentHashMap<>();
        private final Map<Class<?>, Integer> packet2ID = new ConcurrentHashMap<>();

        private final Map<Class<?>, PacketReader<?>> readers = new ConcurrentHashMap<>();
        private final Map<Class<?>, PacketWriter<?>> writers = new ConcurrentHashMap<>();

        public void addPacket(int packetID, Class<?> packetClass, PacketReader<?> reader, PacketWriter<?> writer) {
            if (packet2ID.containsKey(packetClass)) return;
            if (id2Packet.containsKey(packetID))
                throw new IllegalArgumentException(STR."ID \{packetID} is already used by \{getPacket(packetID).getName()}");

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
