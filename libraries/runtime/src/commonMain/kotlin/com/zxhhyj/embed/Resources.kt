package com.zxhhyj.embed

import okio.*

/**
 * 资源访问入口
 *
 * Entry point for accessing embedded resources
 * @param resourceDirectory 资源目录
 *
 * Resource directory implementation
 * @param inMemoryCutoff 内存缓存阈值（字节），超过此大小的资源将使用 Memory 策略
 *
 * In-memory cutoff threshold (bytes), resources larger than this will use Memory strategy
 * @param cacheDirectory 磁盘缓存目录
 *
 * Disk cache directory
 * @param fileSystem 文件系统
 *
 * File system instance
 */
class Resources(
    private val resourceDirectory: ResourceDirectory,
    private val inMemoryCutoff: Long = IN_MEMORY_CUT_OFF,
    private val cacheDirectory: Path? = getTempDirectory(),
    private val fileSystem: FileSystem? = getFileSystem(),
) {
    private val validatedFiles = mutableSetOf<Path>()

    /**
     * 检查资源是否存在
     *
     * Check if a resource exists
     */
    fun exists(path: String) = resourceDirectory[path] != null

    /**
     * 所有资源路径列表
     *
     * List of all resource paths
     */
    val allPaths: List<String> = resourceDirectory.allPaths

    /**
     * 读取资源为 UTF-8 字符串（内存缓存）
     *
     * Read resource as UTF-8 string (cached in memory)
     */
    fun asString(path: String) = path.toResource().asString

    /**
     * 读取资源为字节数组（内存缓存）
     *
     * Read resource as byte array (cached in memory)
     */
    fun asByteArray(path: String) = path.toResource().asByteArray

    /**
     * 获取资源在磁盘缓存中的文件路径
     *
     * Get the cached file path of a resource on disk
     */
    fun asPath(path: String): String? = ensureFile(resourceDirectory.key, path.toResource())?.toString()

    /**
     * 写入资源到 Sink（自动选择优化策略）
     *
     * Write resource to a Sink (auto-selects optimization strategy)
     */
    fun write(path: String, output: Sink) {
        val resource = path.toResource()
        val size = (resource.chunks.size - 1) * RESOURCE_CHUNK_SIZE + resource.chunks.last().length * 0.75
        val strategy = if (size > inMemoryCutoff) OptimizationStrategy.Memory else OptimizationStrategy.Speed
        write(resource, output, strategy)
    }

    /**
     * 写入资源到 Sink（指定优化策略）
     *
     * Write resource to a Sink (with explicit optimization strategy)
     */
    fun write(path: String, output: Sink, optimizationStrategy: OptimizationStrategy) {
        write(path.toResource(), output, optimizationStrategy)
    }

    private fun write(resource: Resource, output: Sink, optimizationStrategy: OptimizationStrategy) {
        require(fileSystem != null) { "You cannot write to a file on a platform that doesn't have a FileSystem" }

        output.buffer().use { buffer ->
            if (optimizationStrategy == OptimizationStrategy.Speed) {
                buffer.write(resource.asByteArray)
            } else {
                val filePath = ensureFile(resourceDirectory.key, resource)
                if (filePath != null) {
                    fileSystem.source(filePath).use { buffer.writeAll(it) }
                } else {
                    for (chunk in resource.chunks) {
                        buffer.write(chunk.decodeChunk())
                    }
                }
            }
        }
    }

    private fun ensureFile(resourceDirKey: String, resource: Resource): Path? {
        if (fileSystem == null) return null

        return try {
            val cacheDirectory = cacheDirectory(resourceDirKey) ?: return null
            val filePath = cacheDirectory / resource.key

            if (filePath in validatedFiles) {
                return filePath
            }

            if (filePath.exists() && computeHash(filePath, fileSystem) == computeHash(resource.chunks)) {
                validatedFiles.add(filePath)
                return filePath
            }

            filePath.write {
                for (chunk in resource.chunks) {
                    write(chunk.decodeChunk())
                }
            }
            validatedFiles.add(filePath)

            filePath
        } catch (_: Exception) {
            null
        }
    }

    private fun cacheDirectory(resourceDirKey: String) = cacheDirectory?.resolve(resourceDirKey)?.apply { createDirs() }

    private fun String.toResource() = resourceDirectory[this] ?: error("Resource not found: $this")

    internal fun Path.exists() = fileSystem?.exists(this) ?: error("FileSystem not supported")
    internal fun Path.createDirs() = fileSystem?.createDirectories(this) ?: error("FileSystem not supported")
    internal fun <T> Path.write(action: BufferedSink.() -> T) =
        fileSystem?.write(this, false, action) ?: error("FileSystem not supported")
}