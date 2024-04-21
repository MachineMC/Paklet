package org.machinemc.paklet.processors;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.PacketWriter;
import org.machinemc.paklet.serialization.SerializerContext;
import org.machinemc.paklet.utils.ConverterVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import static org.objectweb.asm.Opcodes.*;

/**
 * Writer creator that generates class during runtime that utilize the
 * bytecode modification done by the Paklet plugin.
 * <p>
 * The serialization of those packets is fully automatic and uses no reflection.
 * <p>
 * This is the default provided writer for packets that were modified by the Paklet plugin.
 */
public class GeneratedWriterCreator implements WriterCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T> PacketWriter<T> create(Class<T> packet) {
        if (!ProcessorsUtil.isGeneratedPacketClass(packet))
            throw new UnsupportedOperationException("Generated writer can not be created for " + packet.getName());
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
        for (Field field : ProcessorsUtil.collectSerializableFields(packet)) {
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitLdcInsn(packetType);
            methodVisitor.visitLdcInsn(field.getName());
            methodVisitor.visitVarInsn(ALOAD, 3);
            methodVisitor.visitTypeInsn(CHECKCAST, packetType.getInternalName());
            methodVisitor.visitMethodInsn(
                    INVOKEVIRTUAL,
                    packetType.getInternalName(),
                    "$GET_" + field.getName(),
                    Type.getMethodDescriptor(Type.getType(field.getType())),
                    false
            );
            if (field.getType().isPrimitive())
                ConverterVisitor.convertTopPrimitiveToObject(methodVisitor, Type.getType(field.getType()));
            methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    Type.getInternalName(ProcessorsUtil.class),
                    "setValueForField",
                    Type.getMethodDescriptor(
                            Type.VOID_TYPE,
                            Type.getType(SerializerContext.class),
                            Type.getType(DataVisitor.class),
                            Type.getType(Class.class),
                            Type.getType(String.class),
                            Type.getType(Object.class)
                    ),
                    false
            );
        }
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();

        writer.visitEnd();

        return (Class<? extends PacketWriter<T>>) MethodHandles
                .privateLookupIn(packet, MethodHandles.lookup())
                .defineClass(writer.toByteArray());
    }

}
