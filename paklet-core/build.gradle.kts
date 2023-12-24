plugins {
    id("java-library-convention")
    id("org.machinemc.paklet-plugin")
}

dependencies {
    implementation(project(":paklet-api"))

    implementation(libs.asm)
    implementation(libs.google.gson)
    implementation(libs.netty)

    testAnnotationProcessor(project(":paklet-api-processor"))
}

paklet {
    testEnvironment = true
}