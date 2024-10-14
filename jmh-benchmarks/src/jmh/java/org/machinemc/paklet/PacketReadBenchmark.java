package org.machinemc.paklet;

import io.netty.buffer.Unpooled;
import org.machinemc.paklet.netty.NettyDataVisitor;
import org.machinemc.paklet.packets.ArrayPacket;
import org.machinemc.paklet.packets.CollectionPacket;
import org.machinemc.paklet.packets.SimplePacket;
import org.machinemc.paklet.serialization.SerializerProvider;
import org.machinemc.paklet.serialization.catalogue.DefaultSerializationRules;
import org.machinemc.paklet.serialization.catalogue.DefaultSerializers;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class PacketReadBenchmark {

    PacketFactory factory;

    @Setup
    public void setup() {
        SerializerProvider serializerProvider = new SerializerProviderImpl();
        serializerProvider.addSerializers(DefaultSerializers.class);
        serializerProvider.addSerializationRules(DefaultSerializationRules.class);

        factory = new PacketFactoryImpl(PacketEncoder.varInt(), serializerProvider);
        factory.addPackets(BenchmarkPackets.class);
    }

    @Benchmark
    public void simplePacketRead100() {
        SimplePacket packet = BenchmarkPackets.simplePacket();
        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());
        factory.write(packet, Packet.DEFAULT, visitor);
        for (int i = 0; i < 100; i++) {
            factory.create(Packet.DEFAULT, visitor);
            visitor.readerIndex(0);
        }
    }

    @Benchmark
    public void arrayPacketRead100() {
        ArrayPacket packet = BenchmarkPackets.arrayPacket();
        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());
        factory.write(packet, Packet.DEFAULT, visitor);
        for (int i = 0; i < 100; i++) {
            factory.create(Packet.DEFAULT, visitor);
            visitor.readerIndex(0);
        }
    }

    @Benchmark
    public void collectionPacketRead100() {
        CollectionPacket packet = BenchmarkPackets.collectionPacket();
        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());
        factory.write(packet, Packet.DEFAULT, visitor);
        for (int i = 0; i < 100; i++) {
            factory.create(Packet.DEFAULT, visitor);
            visitor.readerIndex(0);
        }
    }

}
