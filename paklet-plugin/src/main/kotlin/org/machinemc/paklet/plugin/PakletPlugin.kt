package org.machinemc.paklet.plugin

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.withType
import org.machinemc.paklet.plugin.bytecode.PacketExpander
import java.io.File
import java.io.FileReader

/**
 * Entry point for Paklet Gradle plugin.
 */
abstract class PakletPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val tasks = target.tasks.withType<JavaCompile>()
        tasks.forEach { task -> task.doLast {
                val destination = task.destinationDirectory
                if (!destination.asFile.isPresent) return@doLast

                val directory = destination.asFile.get()
                val packetData = File(directory, "paklet-packet-data.json")
                if (!packetData.exists()) return@doLast

                var packets: JsonArray
                JsonParser.parseReader(FileReader(packetData)).asJsonObject.run {
                    packets = getAsJsonArray("packets")
                }

                packets.forEach { packet -> PacketExpander(PluginUtils.classFile(directory, packet.asString)) }
            }
        }
    }

}