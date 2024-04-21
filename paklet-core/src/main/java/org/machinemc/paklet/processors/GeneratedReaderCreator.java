package org.machinemc.paklet.processors;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.PacketReader;
import org.machinemc.paklet.serialization.SerializerContext;
import org.machinemc.paklet.utils.ConverterVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ARETURN;

/**
 * Reader creator that generates class during runtime that utilize the
 * bytecode modification done by the Paklet plugin.
 * <p>
 * The serialization of those packets is fully automatic and uses no reflection.
 * <p>
 * This is the default provided reader for packets that were modified by the Paklet plugin.
 */
public class GeneratedReaderCreator implements ReaderCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T> PacketReader<T> create(Class<T> packet) {
        if (!ProcessorsUtil.isGeneratedPacketClass(packet))
            throw new UnsupportedOperationException("Generated reader can not be created for " + packet.getName());
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
        methodVisitor.visitVarInsn(ASTORE, 3);
        for (Field field : ProcessorsUtil.collectSerializableFields(packet)) {
            methodVisitor.visitVarInsn(ALOAD, 3);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitLdcInsn(packetType);
            methodVisitor.visitLdcInsn(field.getName());
            methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    Type.getInternalName(ProcessorsUtil.class),
                    "getValueForField",
                    Type.getMethodDescriptor(
                            Type.getType(Object.class),
                            Type.getType(SerializerContext.class),
                            Type.getType(DataVisitor.class),
                            Type.getType(Class.class),
                            Type.getType(String.class)
                    ),
                    false
            );
            ConverterVisitor.convertTopObject(methodVisitor, Type.getType(field.getType()));
            methodVisitor.visitMethodInsn(
                    INVOKEVIRTUAL,
                    packetType.getInternalName(),
                    "$SET_" + field.getName(),
                    Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(field.getType())),
                    false
            );
        }
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();

        writer.visitEnd();

        return (Class<? extends PacketReader<T>>) MethodHandles
                .privateLookupIn(packet, MethodHandles.lookup())
                .defineClass(writer.toByteArray());
    }

}
