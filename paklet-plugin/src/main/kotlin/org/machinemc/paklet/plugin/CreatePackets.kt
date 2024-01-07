package org.machinemc.paklet.plugin

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.machinemc.paklet.plugin.bytecode.PacketExpander
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import kotlin.io.path.name

/**
 * Task that modifies packet classes.
 */
abstract class CreatePackets : DefaultTask() {

    /**
     * Path to the directory with compiled classes.
     */
    @get:Input
    abstract val classesPath: Property<String>

    /**
     * Path to the directory with 'paklet-packet-data.json' file.
     */
    @get:Input
    abstract val packetDataPath: Property<String>

    init {
        classesPath.convention("")
        packetDataPath.convention("")
    }

    @Internal var hasJson = false

    @Internal lateinit var sourcesDir: File

    @Internal lateinit var defaultSerializers: JsonArray
    @Internal lateinit var packets: JsonArray

    fun init() {

        val buildDir = project.layout.buildDirectory.asFile.orNull ?: error("Cannot access the build directory")

        val file: File?

        if (classesPath.get().isEmpty() xor packetDataPath.get().isEmpty())
            logger.warn("Both classes path and packet data path need to be defined")

        if (classesPath.get().isEmpty() or packetDataPath.get().isEmpty()) {
            val classesDir = File(buildDir, "classes")
            if (!classesDir.exists()) error("Classes directory does not exist")
            file = Files.walk(classesDir.toPath(), Integer.MAX_VALUE)
                .filter { f -> f.name == "paklet-packet-data.json" }
                .findFirst()
                .map { path -> path.toFile() }
                .orElse(null)
            if (file == null) return
            sourcesDir = file.parentFile
        } else {
            var f = buildDir
            classesPath.get().split("/").forEach { f = File(f, it) }
            if (!f.exists()) error("Classes directory does not exist")
            sourcesDir = f

            f = buildDir
            packetDataPath.get().split("/").forEach { f = File(f, it) }
            file = File(f, "paklet-packet-data.json")
        }

        if (!file.exists()) return

        hasJson = true

        JsonParser.parseReader(FileReader(file)).asJsonObject.run {
            defaultSerializers = getAsJsonArray("defaultSerializers")
            packets = getAsJsonArray("packets")
        }
    }

    fun modifyPacketClasses() {
        if (!hasJson) return
        packets.forEach { packet -> PacketExpander(PluginUtils.classFile(sourcesDir, packet.asString)) }
    }

}