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

paklet {
    testEnvironment = true
}

publishing {
    repositories {
        maven {
            name = "machine"
            url = uri("http://www.machinemc.org/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
            isAllowInsecureProtocol = true
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.machinemc"
            artifactId = "paklet-core"
            version = "1.0.0"
            from(components["java"])
        }
    }
}