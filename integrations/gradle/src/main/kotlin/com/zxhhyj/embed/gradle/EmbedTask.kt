package com.zxhhyj.embed.gradle

import com.zxhhyj.embed.AssetProcessor
import okio.Path.Companion.toOkioPath
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

@CacheableTask
abstract class EmbedTask : DefaultTask() {

    @get:Input
    abstract val packageName: Property<String>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val resourceDirectories: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    init {
        group = "embed"
        description = "Generate Kotlin ResourceDirectory from asset files"
    }

    @TaskAction
    fun generate() {
        val packageNameValue = packageName.get()
        val outputDir = outputDirectory.get().asFile.toOkioPath()

        val directories = resourceDirectories.files.map { it.toOkioPath() }

        require(packageNameValue.isNotEmpty()) { "Package name must not be empty" }
        require(directories.isNotEmpty()) { "You must specify at least one directory for resourceDirectories" }

        logger.lifecycle("Generating ResourceDirectory for package: $packageNameValue")
        logger.lifecycle("Resource directories: ${directories.joinToString(", ")}")
        logger.lifecycle("Output directory: $outputDir")

        val processor = AssetProcessor()
        processor.process(
            directories = directories,
            packageName = packageNameValue,
            baseOutputDir = outputDir
        )

        logger.lifecycle("Successfully generated ResourceDirectory")
    }
}