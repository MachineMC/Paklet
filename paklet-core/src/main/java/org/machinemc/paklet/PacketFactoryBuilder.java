package org.machinemc.paklet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.machinemc.paklet.serializers.SerializerProvider;

import java.io.InputStream;
import java.io.InputStreamReader;

public class PacketFactoryBuilder {

    private final DefaultPacketFactory delegate;

    public static PacketFactoryBuilder create(Serializer<Integer> idSerializer, SerializerProvider serializerProvider) {
        return new PacketFactoryBuilder(idSerializer, serializerProvider);
    }

    private PacketFactoryBuilder(Serializer<Integer> idSerializer, SerializerProvider serializerProvider) {
        delegate = new DefaultPacketFactory(idSerializer, serializerProvider);
    }

    /**
     * Adds new packet information to this factory.
     * <p>
     * Automatically generates readers and writers.
     *
     * @param packetClass class of the packet
     * @param <T> packet
     */
    public <T> PacketFactoryBuilder addPacket(Class<T> packetClass) {
        delegate.addPacket(packetClass);
        return this;
    }

    /**
     * Adds new packet information to the factory.
     *
     * @param packetClass class of the packet
     * @param reader reader for the packet
     * @param writer writer for the packet
     * @param <T> packet
     */
    public <T> PacketFactoryBuilder addPacket(Class<T> packetClass, PacketReader<T> reader, PacketWriter<T> writer) {
        delegate.addPacket(packetClass, reader, writer);
        return this;
    }

    /**
     * Loads default packets marked with {@link Packet} files.
     *
     * @return this
     * @see Packet
     */
    public PacketFactoryBuilder loadDefaults() {
        return loadDefaults(DefaultSerializerProvider.class.getResourceAsStream("/packlet-packet-data.json"));
    }

    /**
     * Loads packets from json.
     * <p>
     * Can be used on platforms where it is impossible to get resources as
     * streams the typical way or to load serializers from other json files.
     *
     * @param is json input
     * @return this
     */
    public PacketFactoryBuilder loadDefaults(InputStream is) {
        try (is) {
            JsonObject json = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
            JsonArray packets = json.getAsJsonArray("packets");
            for (JsonElement element : packets) {
                String className = element.getAsString().replace('/', '.');
                Class<?> packet = Class.forName(className);
                addPacket(packet);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return this;
    }

    /**
     * @return serializer provider of this builder
     */
    public PacketFactory build() {
        return delegate;
    }


}
