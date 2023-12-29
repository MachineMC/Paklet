plugins {
    id("java-library-convention")
}

dependencies {
    implementation(project(":paklet-api"))

    implementation(libs.google.autoservice)
    annotationProcessor(libs.google.autoservice)

    implementation(libs.google.gson)
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.remove("--enable-preview")
}

tasks.withType<Test>().configureEach {
    val args = jvmArgs!!.toMutableList()
    args.remove("--enable-preview")
    jvmArgs(args)
}

tasks.withType<JavaExec>().configureEach {
    val args = jvmArgs!!.toMutableList()
    args.remove("--enable-preview")
    jvmArgs(args)
}