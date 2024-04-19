plugins {
    id("java-library-convention")
    id("org.machinemc.paklet-plugin")
    `maven-publish`
}

dependencies {
    implementation(project(":paklet-api"))

    implementation(libs.asm)
    implementation(libs.google.gson)
    implementation(libs.netty)

    testAnnotationProcessor(project(":paklet-processor"))
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
            artifactId = "paklet-core"
            version = project.version.toString()
            from(components["java"])
        }
    }
}