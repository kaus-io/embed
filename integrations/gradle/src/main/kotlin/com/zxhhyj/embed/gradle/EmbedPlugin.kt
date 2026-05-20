package com.zxhhyj.embed.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class EmbedPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("embed", EmbedExtension::class.java)

        val generateTask = project.tasks.register("generateEmbedResources", EmbedTask::class.java) { task ->
            task.packageName.set(extension.packageName)
            task.resourceDirectories.setFrom(extension.resourceDirectories)
            task.outputDirectory.set(project.layout.buildDirectory.dir("generated/ktembed"))
        }

        project.plugins.withId("org.jetbrains.kotlin.jvm") {
            project.extensions
                .getByType(KotlinJvmProjectExtension::class.java)
                .sourceSets
                .getByName(SourceSet.MAIN_SOURCE_SET_NAME)
                .kotlin.srcDir(generateTask.map { it.outputDirectory })
        }

        project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
            project.afterEvaluate {
                project.extensions.getByType(KotlinMultiplatformExtension::class.java)
                    .sourceSets
                    .getByName("commonMain")
                    .kotlin.srcDir(generateTask.map { it.outputDirectory })
            }
        }

        project.tasks.configureEach { task ->
            if (task.name.startsWith("compile") && task.name.contains("Kotlin")) {
                task.dependsOn(generateTask)
            }
        }
    }
}