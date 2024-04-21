package org.machinemc.paklet;

import org.machinemc.paklet.packets.ArrayPacket;
import org.machinemc.paklet.packets.CollectionPacket;
import org.machinemc.paklet.packets.SimplePacket;

import java.util.List;

public final class BenchmarkPackets {

    private BenchmarkPackets() {
        throw new UnsupportedOperationException();
    }

    public static SimplePacket simplePacket() {
        var packet = new SimplePacket();
        packet.f1 = 20;
        packet.f2 = true;
        packet.f3 = 10;
        packet.f4 = "Hello World!";
        packet.f5 = 1.5f;
        return packet;
    }

    public static ArrayPacket arrayPacket() {
        var packet = new ArrayPacket();
        packet.intArray = new int[] { 1, 2, 3, 4, 5, 6 };
        packet.nestedStringArray = new String[][] { new String[] { "Hello World" }, new String[0] };
        packet.doubleArray = new double[] { 1, 2, 3, 4, 5, 6, 7, 8 };
        return packet;
    }

    public static CollectionPacket collectionPacket() {
        var packet = new CollectionPacket();
        packet.stringList = List.of("Hello", "World", "!", "Foo", "Bar");
        packet.integerCollection = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        packet.nestedStringList = List.of(packet.stringList, packet.stringList);
        return packet;
    }

}
