import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("java-gradle-plugin")
}

group = "com.budius"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {

}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

gradlePlugin {
    plugins {
        create("slowGradleMachine") {
            id = "com.budius.slow-gradle-machine"
            implementationClass = "com.budius.SlowGradleMachine"
        }
    }
}
