plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.asm)
    implementation(libs.google.gson)
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