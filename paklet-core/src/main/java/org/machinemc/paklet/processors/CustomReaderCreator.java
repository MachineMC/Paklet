package org.machinemc.paklet.processors;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.CustomPacket;
import org.machinemc.paklet.PacketReader;
import org.machinemc.paklet.serializers.SerializerContext;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ARETURN;

public class CustomReaderCreator implements ReaderCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T> PacketReader<T> create(Class<T> packet) {
        if (Arrays.stream(packet.getInterfaces()).noneMatch(type -> type == CustomPacket.class))
            throw new UnsupportedOperationException("Custom reader can not be created for " + packet.getName());
        try {
            Class<?> created = createDefaultReaderClass(packet);
            return (PacketReader<T>) created.getConstructor().newInstance();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Class<? extends PacketReader<T>> createDefaultReaderClass(Class<T> packet) throws IllegalAccessException {
        try {
            return (Class<? extends PacketReader<T>>) Class.forName(packet.getName() + "_READER");
        } catch (Exception ignored) { }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        Type packetType = Type.getType(packet);
        Type readerType = Type.getType("L" + packet.getPackageName().replace('.', '/') + "/" + packet.getSimpleName() + "_READER;");

        writer.visit(
                V21,
                ACC_PUBLIC,
                readerType.getInternalName(),
                "L" + Type.getType(PacketReader.class).getInternalName() + "<" + packetType.getDescriptor() + ">;",
                Type.getType(Object.class).getInternalName(),
                new String[] {Type.getType(PacketReader.class).getInternalName()}
        );

        MethodVisitor methodVisitor = writer.visitMethod(
                ACC_PUBLIC,
                "<init>",
                Type.getMethodDescriptor(Type.VOID_TYPE),
                null,
                new String[0]
        );
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(
                INVOKESPECIAL,
                Type.getInternalName(Object.class),
                "<init>",
                Type.getMethodDescriptor(Type.VOID_TYPE),
                false
        );
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();

        methodVisitor = writer.visitMethod(
                ACC_PUBLIC,
                "read",
                Type.getMethodDescriptor(
                        Type.getType(Object.class),
                        Type.getType(SerializerContext.class),
                        Type.getType(DataVisitor.class)
                ),
                null,
                new String[0]
        );
        methodVisitor.visitTypeInsn(NEW, packetType.getInternalName());
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitMethodInsn(
                INVOKESPECIAL,
                packetType.getInternalName(),
                "<init>",
                Type.getMethodDescriptor(Type.VOID_TYPE),
                false
        );
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitMethodInsn(
                INVOKEVIRTUAL,
                packetType.getInternalName(),
                "construct",
                Type.getMethodDescriptor(
                        Type.VOID_TYPE,
                        Type.getType(SerializerContext.class),
                        Type.getType(DataVisitor.class)
                ),
                false
        );
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();

        writer.visitEnd();

        return (Class<? extends PacketReader<T>>) MethodHandles
                .privateLookupIn(packet, MethodHandles.lookup())
                .defineClass(writer.toByteArray());
    }

}
