package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.Packet;
import org.machinemc.paklet.modifiers.SerializeWith;
import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.serialization.SerializerContext;
import org.machinemc.paklet.test.TestPackets;

@Packet(id = 6, catalogue = TestPackets.class)
public class CustomSerializerPacket {

    public @SerializeWith(CustomStringSerializer.class) String content;

    private static class CustomStringSerializer implements Serializer<String> {

        @Override
        public void serialize(SerializerContext context, DataVisitor visitor, String s) {
            visitor.writeInt(1);
            char[] content = s.toCharArray();
            visitor.writeInt(content.length);
            for (char c : content) visitor.writeChar(c);
        }

        @Override
        public String deserialize(SerializerContext context, DataVisitor visitor) {
            if (visitor.readInt() != 1) throw new IllegalStateException();
            char[] content = new char[visitor.readInt()];
            for (int i = 0; i < content.length; i++) content[i] = visitor.readChar();
            return new String(content);
        }

    }

}
