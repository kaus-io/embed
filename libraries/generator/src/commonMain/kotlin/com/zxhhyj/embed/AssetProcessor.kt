package com.zxhhyj.embed

import okio.Buffer
import okio.FileSystem
import okio.Path
import okio.SYSTEM

private const val MAX_CHUNKS_PER_KOTLIN_FILE = 100

class AssetProcessor(private val fileSystem: FileSystem = FileSystem.SYSTEM) {
    fun process(
        directories: List<Path>,
        packageName: String,
        baseOutputDir: Path,
        ignore: (Path) -> Boolean = { false },
    ) {
        val filePaths = directories.flatMap { scanDirectoryForPaths(path = it, ignore = ignore) }
        val resourceMappings = generateResourceFiles(packageName, filePaths, baseOutputDir)
        val resourceDirectoryContent = ResourceDirectoryGenerator.generate(packageName, resourceMappings)
        writeFile(baseOutputDir, packageName, "ResourceDirectory", resourceDirectoryContent)
    }

    private fun scanDirectoryForPaths(path: Path, baseDir: Path = path, ignore: (Path) -> Boolean): List<FilePathInfo> {
        if (!path.exists || !path.isDirectory) return listOf()

        return buildList {
            path.list().forEach {
                when {
                    ignore(it) -> Unit
                    it.isDirectory -> addAll(scanDirectoryForPaths(it, baseDir, ignore))
                    else -> {
                        val relativePath = it.normalized().toString()
                            .removePrefix(baseDir.normalized().toString())
                            .trimStart('/', '\\')

                        val key = relativePath.replace("/", "_").replace("\\", "_").replace(".", "_")

                        add(FilePathInfo(absolutePath = it, relativePath = relativePath, key = key))
                    }
                }
            }
        }
    }

    private fun generateResourceFiles(
        packageName: String,
        filePaths: List<FilePathInfo>,
        baseOutputDir: Path
    ): List<ResourceMapping> {
        val content = StringBuilder()
        content.appendLine("package $packageName")
        content.appendLine()

        var fileCounter = 0
        var chunkCount = 0

        fun writeFile() {
            if (chunkCount > 0) {
                writeFile(baseOutputDir, packageName, "ResourceChunks${++fileCounter}", content.toString())
                content.clear()
                content.appendLine("package $packageName")
                content.appendLine()
            }
            chunkCount = 0
        }

        return buildList {
            filePaths.forEach { fileInfo ->
                val chunkVariableName = "RESOURCE_${size + 1}"

                content.append("public val $chunkVariableName: List<String> = listOf(\n")

                val chunkBuffer = Buffer()
                var chunkSize = 0L

                fun writeChunk() {
                    content.append("    \"${chunkBuffer.encodeChunk()}\",\n")
                    chunkBuffer.clear()
                    chunkCount++
                    chunkSize = 0L
                }

                fileSystem.read(fileInfo.absolutePath) {
                    while (!exhausted()) {
                        chunkSize += buffer.size
                        chunkBuffer.write(buffer.readByteString())
                        if (chunkSize >= RESOURCE_CHUNK_SIZE) {
                            writeChunk()
                        }
                    }

                    if (chunkSize > 0) {
                        writeChunk()
                    }
                }

                content.appendLine(")")

                add(ResourceMapping(fileInfo.relativePath, chunkVariableName, fileInfo.key))

                if (chunkCount > MAX_CHUNKS_PER_KOTLIN_FILE) {
                    writeFile()
                }
            }

            writeFile()
        }
    }

    private fun writeFile(baseOutputDir: Path, packageName: String, name: String, content: String) {
        val folders = packageName.split(".")

        folders.fold(baseOutputDir) { dir, folder -> dir.resolve(folder) }
            .resolve("$name.kt")
            .mkDirs()
            .write(content)
    }

    private data class FilePathInfo(val absolutePath: Path, val relativePath: String, val key: String)

    private fun Path.list(): List<Path> = fileSystem.list(this)
    private val Path.isDirectory: Boolean get() = fileSystem.metadataOrNull(this)?.isDirectory == true
    private val Path.exists: Boolean get() = fileSystem.exists(this)
    private fun Path.write(text: String) = fileSystem.write(this, false) { writeUtf8(text) }

    private fun Path.mkDirs(): Path {
        if (exists) return this

        if (isDirectory) {
            fileSystem.createDirectories(this, false)
        } else {
            fileSystem.createDirectories(this.parent!!, false)
        }

        return this
    }
}