package org.machinemc.paklet.processors;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.PacketLogic;
import org.machinemc.paklet.PacketWriter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.RETURN;

public class CustomWriterCreator implements WriterCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T> PacketWriter<T> create(Class<T> packet) {
        if (Arrays.stream(packet.getInterfaces()).noneMatch(type -> type == PacketLogic.class))
            throw new UnsupportedOperationException(STR."Custom writer can not be created for \{packet.getName()}");
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
            return (Class<? extends PacketWriter<T>>) Class.forName(STR."\{packet.getName()}_WRITER");
        } catch (Exception _) { }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        Type packetType = Type.getType(packet);
        Type readerType = Type.getType(STR."L\{packet.getPackageName().replace('.', '/')}/\{packet.getSimpleName()}_WRITER;");

        writer.visit(
                V21,
                ACC_PUBLIC,
                readerType.getInternalName(),
                STR."L\{Type.getType(PacketWriter.class).getInternalName()}<\{packetType.getDescriptor()}>;",
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
                "accept",
                Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Object.class), Type.getType(Object.class)),
                null,
                new String[0]
        );
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitTypeInsn(CHECKCAST, packetType.getInternalName());
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitMethodInsn(
                INVOKEVIRTUAL,
                packetType.getInternalName(),
                "deconstruct",
                Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(DataVisitor.class)),
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
