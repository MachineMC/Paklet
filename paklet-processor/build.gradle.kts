plugins {
    id("java-library-convention")
    `maven-publish`
}

dependencies {
    implementation(project(":paklet-api"))

    implementation(libs.google.autoservice)
    annotationProcessor(libs.google.autoservice)

    implementation(libs.google.gson)
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
            artifactId = "paklet-processor"
            version = project.version.toString()
            from(components["java"])
        }
    }
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