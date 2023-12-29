package org.machinemc.paklet.plugin

import org.objectweb.asm.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.function.Function

/**
 * Utils for Paklet Gradle plugin.
 */
object PluginUtils {

    /**
     * Returns a class file for a class with given name in provided sources directory.
     * <p>
     * The provided directory is the root directory of the classes, not the same
     * directory the class is in.
     *
     * @param directory sources directory
     * @param internalName internal name of the class
     * @return file of the class
     */
    @JvmStatic fun classFile(directory: File, internalName: String): File {
        var file = directory
        val path = internalName.split("/").toMutableList()
        val fileName = "${path.last()}.class"
        path.removeLast()
        path.forEach { next -> file = File(file, next) }
        return File(file, fileName)
    }

    /**
     * @param dotPath dot path of the class
     * @return type of the class
     */
    @JvmStatic fun getType(dotPath: String?): Type {
        return Type.getType(getDescriptor(dotPath!!))
    }

    /**
     * @param internalName internal name of the class
     * @return type of the class
     */
    @JvmStatic fun getTypeFromInternal(internalName: String): Type {
        return Type.getType(getDescriptorFromInternal(internalName))
    }

    /**
     * @param dotPath dot path of the class
     * @return descriptor of the class
     */
    @JvmStatic fun getDescriptor(dotPath: String): String {
        return "L${dotPath.replace(".", "/")};"
    }

    /**
     * @param internalName internal name of the class
     * @return descriptor of the class
     */
    @JvmStatic fun getDescriptorFromInternal(internalName: String): String {
        return "L$internalName;"
    }

    /**
     * Reads the given class file using provided visitor.
     *
     * @param file class file
     * @param visitor visitor
     */
    @JvmStatic fun readClass(file: File, visitor: ClassVisitor) {
        FileInputStream(file).use { input -> ClassReader(input).accept(visitor, 0) }
    }

    /**
     * Reads the given class file using provided visitor and applies the changes.
     *
     * @param file class file
     * @param visitor visitor
     * @param bytes updated class data
     */
    @JvmStatic fun <T : ClassVisitor> readClassAndModify(file: File, visitor: T, bytes: Function<T, ByteArray>) {
        FileInputStream(file).use { input -> ClassReader(input).accept(visitor, 0) }
        file.writeBytes(bytes.apply(visitor))
    }

    /**
     * Creates a new class from given data.
     *
     * @param classFile class file
     * @param bytes class data
     */
    @JvmStatic fun writeClass(classFile: File, bytes: ByteArray) {
        if (!classFile.exists() && !classFile.createNewFile())
            throw IOException("Failed to create the class file ${classFile.name}")
        classFile.writeBytes(bytes)
    }

}