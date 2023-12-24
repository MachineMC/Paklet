rootProject.name = "Paklet"

include("paklet-api")
include("paklet-api-processor")
include("paklet-core")

pluginManagement {
    includeBuild("build-logic")
    includeBuild("paklet-api-plugin")
}

dependencyResolutionManagement {
    versionCatalogs {

        create("libs") {
            val jetbrainsAnnotations: String by settings
            library("jetbrains-annotations", "org.jetbrains:annotations:$jetbrainsAnnotations")

            val junit: String by settings
            library("junit-api", "org.junit.jupiter:junit-jupiter-api:$junit")
            library("junit-engine", "org.junit.jupiter:junit-jupiter-engine:$junit")
            library("junit-params", "org.junit.jupiter:junit-jupiter-params:$junit")

            val asm: String by settings
            library("asm", "org.ow2.asm:asm:$asm")
            library("asm-commons", "org.ow2.asm:asm-commons:$asm")

            val netty: String by settings
            library("netty", "io.netty:netty-all:$netty")

            val googleAutoservice: String by settings
            library("google-autoservice", "com.google.auto.service:auto-service:$googleAutoservice")

            val googleGson: String by settings
            library("google-gson", "com.google.code.gson:gson:$googleGson")
        }


    }
}
