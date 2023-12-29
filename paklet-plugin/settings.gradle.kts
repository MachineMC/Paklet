import java.io.FileReader
import java.util.Properties

val rootProperties = Properties()
rootProperties.load(FileReader(File(rootDir.parent, "gradle.properties")))

dependencyResolutionManagement {
    versionCatalogs {

        create("libs") {
            val asm: String by rootProperties
            library("asm", "org.ow2.asm:asm:$asm")
            library("asm-commons", "org.ow2.asm:asm-commons:$asm")

            val googleGson: String by rootProperties
            library("google-gson", "com.google.code.gson:gson:$googleGson")
        }

    }
}