package org.machinemc.paklet.processors;

import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.PacketReader;
import org.machinemc.paklet.modifiers.Ignore;
import org.machinemc.paklet.utils.ConverterVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ARETURN;

public class GeneratedReaderCreator implements ReaderCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T> PacketReader<T> create(Class<T> packet) {
        if (!ProcessorsUtil.isGeneratedPacketClass(packet))
            throw new UnsupportedOperationException(STR."Generated reader can not be created for \{packet.getName()}");
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
            return (Class<? extends PacketReader<T>>) Class.forName(STR."\{packet.getName()}_READER");
        } catch (Exception _) { }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        Type packetType = Type.getType(packet);
        Type readerType = Type.getType(STR."L\{packet.getPackageName().replace('.', '/')}/\{packet.getSimpleName()}_READER;");

        writer.visit(
                V21,
                ACC_PUBLIC,
                readerType.getInternalName(),
                STR."L\{Type.getType(PacketReader.class).getInternalName()}<\{packetType.getDescriptor()}>;",
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
                "apply",
                Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(Object.class)),
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
        methodVisitor.visitVarInsn(ASTORE, 2);
        for (Field field : packet.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) continue;
            if (field.isAnnotationPresent(Ignore.class)) continue;
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitLdcInsn(packetType);
            methodVisitor.visitLdcInsn(field.getName());
            methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    Type.getInternalName(ProcessorsUtil.class),
                    "getValueForField",
                    Type.getMethodDescriptor(
                            Type.getType(Object.class),
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
                    STR."$SET_\{field.getName()}",
                    Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(field.getType())),
                    false
            );
        }
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();

        writer.visitEnd();

        return (Class<? extends PacketReader<T>>) MethodHandles
                .privateLookupIn(packet, MethodHandles.lookup())
                .defineClass(writer.toByteArray());
    }

}
