package org.machinemc.paklet.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.task

/**
 * Entry point for Paklet Gradle plugin.
 */
abstract class PakletPlugin : Plugin<Project> {

    interface PakletPluginExtension {

        val testEnvironment: Property<Boolean>

    }

    override fun apply(target: Project) {
        val extension = target.extensions.create("paklet", PakletPluginExtension::class.java)
        extension.testEnvironment.convention(false)

        val compileJava = target.tasks.named("compileJava").get()
        val compileTestJava = target.tasks.named("compileTestJava").get()

        val createPackets = target.task<CreatePackets>("createPackets")
        createPackets.run {
            doLast {
                val targetTask = if (extension.testEnvironment.get()) compileTestJava else compileJava
                if (!targetTask.state.didWork) {
                    didWork = false
                    return@doLast
                }
                init()
                modifyPacketClasses()
            }
        }
        compileJava.finalizedBy(createPackets)
        compileTestJava.finalizedBy(createPackets)
    }

}