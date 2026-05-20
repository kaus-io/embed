package com.zxhhyj.embed

import okio.Path.Companion.toPath
import java.io.File

fun main(args: Array<String>) {
    var packageName = ""
    var resourceDirs = listOf<String>()
    var outputDir = ""

    var i = 0
    while (i < args.size) {
        when (args[i]) {
            "--package" -> {
                i++
                packageName = args[i]
            }
            "--resources" -> {
                i++
                resourceDirs = args[i].split(File.pathSeparator)
            }
            "--output" -> {
                i++
                outputDir = args[i]
            }
        }
        i++
    }

    require(packageName.isNotEmpty()) { "--package is required" }
    require(resourceDirs.isNotEmpty()) { "--resources is required" }
    require(outputDir.isNotEmpty()) { "--output is required" }

    val processor = AssetProcessor()
    processor.process(
        directories = resourceDirs.map { it.toPath() },
        packageName = packageName,
        baseOutputDir = outputDir.toPath()
    )
}