plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

group = "org.machinemc"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.asm)
    implementation(libs.google.gson)
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
            artifactId = "paklet-plugin"
            version = "1.0.0"
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