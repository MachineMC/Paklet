plugins {
    id("java-library-convention")
    `maven-publish`
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
            artifactId = "paklet-api"
            version = "1.0.0"
            from(components["java"])
        }
    }
}