import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.BufferedReader
import java.io.FileReader
import java.util.*

plugins {
    kotlin("jvm") version "1.7.10"
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.0.0"
}

group = "io.github.budius"
version = "0.0.2"

repositories {
    mavenCentral()
}

dependencies {

}

val local = Properties().apply {
    val file = File(rootProject.rootDir, "local.properties")
    if (file.exists()) {
        BufferedReader(FileReader(file)).use { load(it) }
    }
}

project.ext["gradle.publish.key"] = local["gradle.publish.key"]
project.ext["gradle.publish.secret"] = local["gradle.publish.secret"]

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

pluginBundle {
    website = "https://github.com/budius/slowGradleMachine"
    vcsUrl = "https://github.com/budius/slowGradleMachine.git"
    tags = listOf("performance", "speed", "slow", "time", "tracker")
}

gradlePlugin {
    plugins {
        create("slowMachine") {
            id = "${group}.slow-machine"
            displayName = "Is my computer slow?"
            description = "Inspired by a real life scenario, this small utility tracks how long " +
                    "you've been sitting around and waiting for your computer to compile. " +
                    "It saves in a simple properties format and could be helpful to make the IT department" +
                    "to get you a new computer with faster processor and more RAM."
            implementationClass = "com.budius.SlowGradleMachine"
        }
    }
}
