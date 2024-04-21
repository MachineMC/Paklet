plugins {
    id("java-library-convention")
    id("org.machinemc.paklet-plugin")
    alias(libs.plugins.champeau.jmh)
}

dependencies {
    implementation(libs.netty)

    jmhAnnotationProcessor(project(":paklet-processor"))

    implementation(project(":paklet-api"))
    implementation(project(":paklet-core"))
}
