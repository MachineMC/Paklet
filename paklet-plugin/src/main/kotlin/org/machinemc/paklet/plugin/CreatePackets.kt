package org.machinemc.paklet.plugin

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.machinemc.paklet.plugin.bytecode.PacketExpander
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import kotlin.io.path.name

/**
 * Task that modifies packet classes.
 */
open class CreatePackets : DefaultTask() {

    @Internal var hasJson = false

    @Internal lateinit var sourcesDir: File

    @Internal lateinit var defaultSerializers: JsonArray
    @Internal lateinit var packets: JsonArray

    fun init() {

        val buildDir = project.layout.buildDirectory.asFile.orNull ?: error("Cannot access the build directory")
        val classesDir = File(buildDir, "classes")
        if (!classesDir.exists()) error("Classes directory does not exist")

        val file = Files.walk(classesDir.toPath(), Integer.MAX_VALUE)
            .filter { file -> file.name == "packlet-packet-data.json" }
            .findFirst()
            .map { path -> path.toFile() }
            .orElse(null)

        if (file == null) return
        hasJson = true

        JsonParser.parseReader(FileReader(file)).asJsonObject.run {
            defaultSerializers = getAsJsonArray("defaultSerializers")
            packets = getAsJsonArray("packets")
        }

        sourcesDir = file.parentFile
    }

    fun modifyPacketClasses() {
        if (!hasJson) return
        packets.forEach { packet -> PacketExpander(PluginUtils.classFile(sourcesDir, packet.asString)) }
    }

}