package org.machinemc.paklet.test;

import org.machinemc.paklet.PacketEncoder;
import org.machinemc.paklet.PacketFactory;
import org.machinemc.paklet.PacketFactoryImpl;
import org.machinemc.paklet.SerializerProviderImpl;
import org.machinemc.paklet.serialization.SerializerProvider;
import org.machinemc.paklet.serialization.catalogue.DefaultSerializationRules;
import org.machinemc.paklet.serialization.catalogue.DefaultSerializers;

public final class TestUtil {

    private TestUtil() {
        throw new UnsupportedOperationException();
    }

    public static PacketFactory createFactory() {
        SerializerProvider serializerProvider = new SerializerProviderImpl();
        serializerProvider.addSerializers(DefaultSerializers.class);
        serializerProvider.addSerializationRules(DefaultSerializationRules.class);

        PacketFactory packetFactory = new PacketFactoryImpl(PacketEncoder.varInt(), serializerProvider);
        packetFactory.addPackets(TestPackets.class);

        return packetFactory;
    }

}
