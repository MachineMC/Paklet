package org.machinemc.paklet.plugin.bytecode

import org.machinemc.paklet.plugin.PluginUtils
import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*
import java.io.File

/**
 * Byte code modifier that checks if provided file is legitimate packet class
 * and if so, adds internal getters and setters later on used for fast packet
 * serialization and deserialization.
 */
class PacketExpander(file: File) {

    data class Field(val name: String, val type: Type)

    private lateinit var type: Type
    private var id = -1
    private val fields: MutableList<Field> = ArrayList()

    init {
        PluginUtils.readClass(file, PacketExtractor())
        PluginUtils.readClass(file, FieldExtractor())
        PluginUtils.readClassAndModify(file, OldMethodRemover()) { visitor -> visitor.toByteArray() }
        PluginUtils.readClassAndModify(file, GetterSetterInjector()) { visitor -> visitor.toByteArray() }
    }

    inner class PacketExtractor : ClassVisitor(ASM9) {

        private var noArgsConstructor = false
        private val packet: Type = PluginUtils.getType("org.machinemc.paklet.Packet")
        private val customPacket: Type = PluginUtils.getType("org.machinemc.paklet.CustomPacket")

        override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
            type = PluginUtils.getTypeFromInternal(name!!)
            val interfaceTypes = interfaces!!.map { PluginUtils.getTypeFromInternal(it) }.toList()
            if (superName != Type.getType(Object::class.java).internalName && !interfaceTypes.contains(customPacket))
                throw IllegalStateException("Packet ${type.internalName} must either not extend any class or implement custom packet logic")
            super.visit(version, access, name, signature, superName, interfaces)
        }

        override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor? {
            if (name == "<init>" && descriptor == "()V" && access and ACC_PUBLIC == ACC_PUBLIC)
                noArgsConstructor = true
            return super.visitMethod(access, name, descriptor, signature, exceptions)
        }

        override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {
            if (descriptor == packet.descriptor)
                return AnnotationExtractor(super.visitAnnotation(descriptor, visible))
            return super.visitAnnotation(descriptor, visible)
        }

        override fun visitEnd() {
            if (!noArgsConstructor)
                throw IllegalStateException("Packet ${type.internalName} has no public constructor without arguments")
            super.visitEnd()
        }

        inner class AnnotationExtractor(delegate: AnnotationVisitor?) : AnnotationVisitor(ASM9, delegate) {

            override fun visit(name: String?, value: Any?) {
                if (name == "value") id = value as Int
                super.visit(name, value)
            }

        }

    }

    inner class FieldExtractor : ClassVisitor(ASM9) {

        private var lastField: Field? = null

        override fun visitField(access: Int, name: String?, descriptor: String?, signature: String?, value: Any?): FieldVisitor {
            lastField = if (access and ACC_TRANSIENT == 0) Field(name!!, Type.getType(descriptor)) else null
            if (lastField != null && access and ACC_FINAL != 0 && access and ACC_STATIC == 0)
                throw IllegalStateException("Not static packet fields can not be marked as final - ${type.internalName}.${name}")
            return AnnotationChecker(super.visitField(access, name, descriptor, signature, value))
        }

        inner class AnnotationChecker(delegate: FieldVisitor?) : FieldVisitor(ASM9, delegate) {

            private var ignored = false
            private val ignore: Type = PluginUtils.getType("org.machinemc.paklet.modifiers.Ignore")

            override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {
                if (descriptor == ignore.descriptor) ignored = true
                return super.visitAnnotation(descriptor, visible)
            }

            override fun visitEnd() {
                if (!ignored && lastField != null) this@PacketExpander.fields.add(lastField!!)
                lastField = null
                super.visitEnd()
            }

        }

    }

    inner class OldMethodRemover : ClassVisitor(ASM9, ClassWriter(ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)) {

        override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor? {
            if (name!!.startsWith("\$GET_") or name.startsWith("\$SET_"))
                return null
            return super.visitMethod(access, name, descriptor, signature, exceptions)
        }

        fun toByteArray(): ByteArray {
            return (super.getDelegate() as ClassWriter).toByteArray()
        }

    }

    inner class GetterSetterInjector : ClassVisitor(ASM9, ClassWriter(ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)) {

        override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
            super.visit(version, access, name, signature, superName, interfaces)
            fields.forEach { field -> createGetterSetter(field) }
        }

        private fun createGetterSetter(field: Field) {
            visitMethod(ACC_PUBLIC or ACC_SYNTHETIC, "\$GET_${field.name}", "()${field.type.descriptor}", null, arrayOf<String>()).run {
                visitVarInsn(ALOAD, 0)
                visitFieldInsn(GETFIELD, type.internalName, field.name, field.type.descriptor)
                visitInsn(field.type.getOpcode(IRETURN))
                visitMaxs(0, 0)
                visitEnd()
            }
            visitMethod(ACC_PUBLIC or ACC_SYNTHETIC, "\$SET_${field.name}", "(${field.type.descriptor})V", null, arrayOf<String>()).run {
                visitVarInsn(ALOAD, 0)
                visitVarInsn(field.type.getOpcode(ILOAD), 1)
                visitFieldInsn(PUTFIELD, type.internalName, field.name, field.type.descriptor)
                visitInsn(RETURN)
                visitMaxs(0, 0)
                visitEnd()
            }
        }

        fun toByteArray(): ByteArray {
            return (super.getDelegate() as ClassWriter).toByteArray()
        }

    }

}