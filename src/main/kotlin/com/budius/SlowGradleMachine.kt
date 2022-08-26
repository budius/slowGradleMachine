package com.budius

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class SlowGradleMachine : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val listener = gradle.sharedServices.registerIfAbsent(
                "buildServiceListener", BuildListenerService::class.java
            ) {
                it.parameters.file = File(rootProject.rootDir, "slowMachine.properties")
            }
            listener.get()
        }
    }
}

abstract class BuildListenerService : BuildService<BuildListenerService.Params>, AutoCloseable {

    companion object {
        private const val COMMENTS = "  Auto-generated by SlowGradleMachine plugin.\n" +
                "  Don't forget to add this file to your .gitignore\n\n"
        private const val KEY_TOTAL_MS = "total-wait-time-in-milliseconds"
        private const val KEY_TOTAL = "total-wait-time"
        private const val KEY_LAST = "last-wait-time"
        private const val KEY_SINCE = "awaiting-since"

        private val DT_FORMAT = DateTimeFormatter.ISO_DATE_TIME
        private val DU_FORMAT = DateTimeFormatter.ISO_LOCAL_TIME

        private fun formatDuration(d: Long): String {
            return DU_FORMAT.format(Duration.ofMillis(d).addTo(LocalTime.of(0, 0)))
        }
    }

    interface Params : BuildServiceParameters {
        var file: File
    }

    private val startTime = System.currentTimeMillis()

    init {
        val file = parameters.file
        file.parentFile?.mkdirs()

        if (file.exists().not()) {
            val prop = Properties()
            val dateTime = DT_FORMAT.format(LocalDateTime.now())
            prop.setProperty(KEY_SINCE, dateTime)
            prop.save()
        }
    }

    override fun close() {
        val endTime = System.currentTimeMillis()
        val runTime = endTime - startTime

        val file = parameters.file
        file.parentFile?.mkdirs()

        val prop = Properties().apply {
            if (file.exists()) {
                BufferedReader(FileReader(file)).use { load(it) }
            }
        }

        val accumulatedTime = prop.getProperty(KEY_TOTAL_MS)?.toLong() ?: 0L
        val totalTime = accumulatedTime + runTime


        prop.setProperty(KEY_TOTAL_MS, totalTime.toString())
        prop.setProperty(KEY_TOTAL, formatDuration(totalTime))
        prop.setProperty(KEY_LAST, formatDuration(runTime))
        prop.save()
    }

    private fun Properties.save() {
        BufferedWriter(FileWriter(parameters.file)).use { writer ->
            this.store(writer, COMMENTS)
        }
    }
}
