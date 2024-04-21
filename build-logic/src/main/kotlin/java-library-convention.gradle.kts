plugins {
    java
    `java-library`
}

val group: String by project
setGroup(group)

val version: String by project
setVersion(version)

val libs = project.rootProject
    .extensions
    .getByType(VersionCatalogsExtension::class)
    .named("libs")

//
// Repositories and Dependencies
//

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.findLibrary("jetbrains-annotations").get())

    testImplementation(libs.findLibrary("junit-api").get())
    testRuntimeOnly(libs.findLibrary("junit-engine").get())
    testImplementation(libs.findLibrary("junit-params").get())
}

//
// Java configuration
//

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
}

//
// Task configurations
//

tasks {
    withType<JavaCompile> {
        options.release.set(21)
        options.encoding = Charsets.UTF_8.name()
    }
    test {
        useJUnitPlatform()
    }
}