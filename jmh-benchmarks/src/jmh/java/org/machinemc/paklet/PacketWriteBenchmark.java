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
public class PacketWriteBenchmark {

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
    public void simplePacketWrite100() {
        SimplePacket packet = BenchmarkPackets.simplePacket();
        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());
        for (int i = 0; i < 100; i++) {
            factory.write(packet, visitor);
            visitor.writerIndex(0);
        }
    }

    @Benchmark
    public void arrayPacketWrite100() {
        ArrayPacket packet = BenchmarkPackets.arrayPacket();
        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());
        for (int i = 0; i < 100; i++) {
            factory.write(packet, visitor);
            visitor.writerIndex(0);
        }
    }

    @Benchmark
    public void collectionPacketWrite100() {
        CollectionPacket packet = BenchmarkPackets.collectionPacket();
        DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());
        for (int i = 0; i < 100; i++) {
            factory.write(packet, visitor);
            visitor.writerIndex(0);
        }
    }

}
