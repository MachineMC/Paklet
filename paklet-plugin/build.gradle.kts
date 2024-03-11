import java.io.FileReader
import java.util.*

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

val rootProperties = Properties()
rootProperties.load(FileReader(File(rootDir.parent, "gradle.properties")))

val group: String by rootProperties
setGroup(group)

val version: String by rootProperties
setVersion(version)

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.asm)
    implementation(libs.google.gson)
}

java {
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "machine"
            url = uri("https://repo.machinemc.org/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.machinemc"
            artifactId = "paklet-plugin"
            version = project.version.toString()
            from(components["kotlin"])
        }
    }
}

gradlePlugin {
    plugins {
        create("pakletPlugin") {
            id = "org.machinemc.paklet-plugin"
            implementationClass = "org.machinemc.paklet.plugin.PakletPlugin"
        }
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "21"
        }
    }
}