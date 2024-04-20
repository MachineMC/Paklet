package org.machinemc.paklet.processors;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.CustomPacket;
import org.machinemc.paklet.PacketWriter;
import org.machinemc.paklet.serialization.SerializerContext;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.RETURN;

/**
 * Writer creator that generates class during runtime that utilize the
 * logic implemented by the user.
 * <p>
 * This is the default provided writer for packets that implement {@link CustomPacket}.
 */
public class CustomWriterCreator implements WriterCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T> PacketWriter<T> create(Class<T> packet) {
        if (Arrays.stream(packet.getInterfaces()).noneMatch(type -> type == CustomPacket.class))
            throw new UnsupportedOperationException("Custom writer can not be created for " + packet.getName());
        try {
            Class<?> created = createDefaultWriterClass(packet);
            return (PacketWriter<T>) created.getConstructor().newInstance();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Class<? extends PacketWriter<T>> createDefaultWriterClass(Class<T> packet) throws IllegalAccessException {
        try {
            return (Class<? extends PacketWriter<T>>) Class.forName(packet.getName() + "_WRITER");
        } catch (Exception ignored) { }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        Type packetType = Type.getType(packet);
        Type readerType = Type.getType("L" + packet.getPackageName().replace('.', '/') + "/" + packet.getSimpleName() + "_WRITER;");

        writer.visit(
                V21,
                ACC_PUBLIC,
                readerType.getInternalName(),
                "L" + Type.getType(PacketWriter.class).getInternalName() + "<" + packetType.getDescriptor() + ">;",
                Type.getType(Object.class).getInternalName(),
                new String[] {Type.getType(PacketWriter.class).getInternalName()}
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
                "write",
                Type.getMethodDescriptor(
                        Type.VOID_TYPE,
                        Type.getType(SerializerContext.class),
                        Type.getType(DataVisitor.class),
                        Type.getType(Object.class)
                ),
                null,
                new String[0]
        );
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitTypeInsn(CHECKCAST, packetType.getInternalName());
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitMethodInsn(
                INVOKEVIRTUAL,
                packetType.getInternalName(),
                "deconstruct",
                Type.getMethodDescriptor(
                        Type.VOID_TYPE,
                        Type.getType(SerializerContext.class),
                        Type.getType(DataVisitor.class)
                ),
                false
        );
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();

        writer.visitEnd();

        return (Class<? extends PacketWriter<T>>) MethodHandles
                .privateLookupIn(packet, MethodHandles.lookup())
                .defineClass(writer.toByteArray());
    }

}
