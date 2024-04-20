package org.machinemc.paklet;

import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.paklet.netty.NettyDataVisitor;
import org.machinemc.paklet.processors.*;
import org.machinemc.paklet.serialization.SerializerContext;
import org.machinemc.paklet.serialization.SerializerProvider;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Default implementation of packet factory.
 */
public class PacketFactoryImpl implements PacketFactory {

    private final PacketEncoder encoder;
    private final SerializerProvider serializerProvider;

    private final Map<Class<?>, PacketGroup> packet2Group = new ConcurrentHashMap<>();
    private final Map<String, PacketGroup> groups = new ConcurrentHashMap<>();

    public PacketFactoryImpl(PacketEncoder encoder, SerializerProvider serializerProvider) {
        this.encoder = encoder;
        this.serializerProvider = serializerProvider;
    }

    @Override
    public <PacketType> void addPacket(Class<PacketType> packetClass) {
        ReaderCreator readerCreator;
        WriterCreator writerCreator;

        if (CustomPacket.class.isAssignableFrom(packetClass)) {
            readerCreator = new CustomReaderCreator();
            writerCreator = new CustomWriterCreator();
        } else if (ProcessorsUtil.isGeneratedPacketClass(packetClass)) {
            readerCreator = new GeneratedReaderCreator();
            writerCreator = new GeneratedWriterCreator();
        } else {
            readerCreator = new ProxyReaderCreator();
            writerCreator = new ProxyWriterCreator();
        }

        addPacket(packetClass, readerCreator.create(packetClass), writerCreator.create(packetClass));
    }

    @Override
    public <PacketType> void addPacket(Class<PacketType> packetClass, PacketReader<PacketType> reader, PacketWriter<PacketType> writer) {
        Packet annotation = packetClass.getAnnotation(Packet.class);
        if (annotation == null) throw new IllegalArgumentException("Class " + packetClass.getName() + " is not a valid packet class");
        addPacket(packetClass, reader, writer, computePacketID(packetClass), annotation.group());
    }

    @Override
    public <PacketType> void addPacket(Class<PacketType> packetClass, PacketReader<PacketType> reader, PacketWriter<PacketType> writer, int packetID, String group) {
        if (packetClass == null || reader == null || writer == null) throw new NullPointerException();
        if (packetID == Packet.INVALID_PACKET) return; // invalid packets should be ignored
        if (packetID < 0) throw new IllegalArgumentException("Invalid packet ID for packet " + packetClass.getName());
        PacketGroup packetGroup = groups.computeIfAbsent(group, PacketGroup::new);
        packetGroup.addPacket(packetID, packetClass, reader, writer); // throws illegal exception if packet ID already exists
        packet2Group.put(packetClass, packetGroup);
    }

    @Override
    public <Catalogue> void addPackets(Class<Catalogue> catalogueClass) {
        addPackets(catalogueClass, catalogueClass::getResourceAsStream);
    }

    @Override
    public <Catalogue> void addPackets(Class<Catalogue> catalogueClass, Function<String, InputStream> resourcesAccessor) {
        try {
            List<Class<?>> classes = CatalogueUtils.getClassesOfCatalogue(catalogueClass, "packets", resourcesAccessor);
            classes.forEach(this::addPacket);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public boolean removePacket(int packetID, String group) {
        PacketGroup packetGroup = groups.get(group);
        if (packetGroup == null) return false;
        return packetGroup.removePacket(packetID);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <PacketType> Optional<Class<PacketType>> getPacketClass(int packetID, String group) {
        return Optional.ofNullable(groups.get(group)).map(g -> (Class<PacketType>) g.getPacket(packetID));
    }

    @Override
    public <PacketType> int getPacketID(Class<PacketType> packetClass) {
        PacketGroup packetGroup = packet2Group.get(packetClass);
        if (packetGroup == null) return -1;
        return packetGroup.getID(packetClass);
    }

    @Override
    public <PacketType> Optional<String> getPacketGroup(Class<PacketType> packetClass) {
        return Optional.ofNullable(packet2Group.get(packetClass)).map(PacketGroup::getName);
    }

    @Override
    public @Unmodifiable Collection<Class<?>> getRegisteredPackets() {
        return Collections.unmodifiableSet(packet2Group.keySet());
    }

    @Override
    public <PacketType> PacketType create(String group, DataVisitor visitor) {
        PacketEncoder.Encoded decoded = encoder.decode(visitor, serializerProvider, group);
        return create(decoded.packetID(), group, decoded.packetData());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <PacketType> PacketType create(int packetID, String group, DataVisitor visitor) {
        if (packetID < 0) throw new IllegalArgumentException("Invalid packet ID: " + packetID);

        PacketGroup packetGroup = groups.get(group);
        if (packetGroup == null) throw new NullPointerException("There is no " + group + " packet group");

        Class<?> packetClass = packetGroup.getPacket(packetID);
        if (packetClass == null) throw new NullPointerException("There is no packet with id " + packetID + " in group " + group);

        PacketReader<?> reader = packetGroup.getReader(packetClass);
        if (reader == null) throw new NullPointerException();

        SerializerContext context = new SerializerContext(null, serializerProvider);
        return (PacketType) reader.read(context, visitor);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <PacketType> void write(PacketType packet, DataVisitor visitor) {
        Class<?> packetClass = packet.getClass();
        PacketGroup packetGroup = packet2Group.get(packetClass);
        if (packetGroup == null) throw new NullPointerException("Packet " + packetClass.getName() + " is not assigned to any group");

        int packetID = packetGroup.getID(packetClass);
        if (packetID < 0) throw new IllegalArgumentException("Invalid packet ID: " + packetID);

        PacketWriter<PacketType> writer = (PacketWriter<PacketType>) packetGroup.getWriter(packetClass);
        if (writer == null) throw new NullPointerException();

        SerializerContext context = new SerializerContext(null, serializerProvider);
        DataVisitor packetData = new NettyDataVisitor(Unpooled.buffer());
        writer.write(context, packetData, packet);

        encoder.encode(visitor, serializerProvider, packetGroup.getName(), new PacketEncoder.Encoded(packetID, packetData));
    }

    private int computePacketID(Class<?> packetClass) {
        Packet annotation = packetClass.getAnnotation(Packet.class);
        if (annotation == null) throw new IllegalArgumentException("Class " + packetClass.getName() + " is not a valid packet class");
        if (annotation.id() == Packet.INVALID_PACKET) return Packet.INVALID_PACKET;
        if (annotation.id() == Packet.DYNAMIC_PACKET) {
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
        return annotation.id();
    }

    static class PacketGroup {

        final String name;

        final Map<Integer, Class<?>> id2Packet = new ConcurrentHashMap<>();
        final Map<Class<?>, Integer> packet2ID = new ConcurrentHashMap<>();

        final Map<Class<?>, PacketReader<?>> readers = new ConcurrentHashMap<>();
        final Map<Class<?>, PacketWriter<?>> writers = new ConcurrentHashMap<>();

        PacketGroup(String name) {
            this.name = Objects.requireNonNull(name, "Packet group name can not be null");
        }

        public String getName() {
            return name;
        }

        void addPacket(int packetID, Class<?> packetClass, PacketReader<?> reader, PacketWriter<?> writer) {
            if (packet2ID.containsKey(packetClass)) return;
            if (id2Packet.containsKey(packetID)) {
                Class<?> existing = getPacket(packetID);
                if (existing == null) throw new NullPointerException();
                throw new IllegalArgumentException("ID " + packetID + " is already used by " + existing.getName());
            }
            id2Packet.put(packetID, packetClass);
            packet2ID.put(packetClass, packetID);
            readers.put(packetClass, reader);
            writers.put(packetClass, writer);
        }

        boolean removePacket(int packetID) {
            Class<?> packetClass = id2Packet.remove(packetID);
            if (packetClass == null) return false;
            packet2ID.remove(packetClass);
            readers.remove(packetClass);
            writers.remove(packetClass);
            return true;
        }

        @Nullable Class<?> getPacket(int packetID) {
            return id2Packet.get(packetID);
        }

        int getID(Class<?> packetClass) {
            Integer id = packet2ID.get(packetClass);
            if (id == null) return -1;
            return id;
        }

        @Nullable PacketReader<?> getReader(Class<?> packetClass) {
            return readers.get(packetClass);
        }

        @Nullable PacketWriter<?> getWriter(Class<?> packetClass) {
            return writers.get(packetClass);
        }

    }

}
