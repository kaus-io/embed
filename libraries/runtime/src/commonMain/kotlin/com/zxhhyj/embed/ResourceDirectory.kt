package com.zxhhyj.embed

import okio.Path

/**
 * 资源目录接口，定义资源查找方式
 *
 * Resource directory interface, defines how resources are looked up
 */
interface ResourceDirectory {
    /**
     * 根据路径获取资源  Get a resource by path
     */
    operator fun get(path: String): Resource?

    /**
     * 资源目录唯一标识
     *
     * Unique identifier for this resource directory
     */
    val key: String

    /**
     * 所有可用资源路径  All available resource paths
     */
    val allPaths: List<String>

    /**
     * 转换为 Resources 实例
     *
     * Convert to a Resources instance
     * @param inMemoryCutoff 内存缓存阈值
     *
     * In-memory cutoff threshold
     * @param cacheDirectory 磁盘缓存目录
     *
     * Disk cache directory
     */
    fun toResources(inMemoryCutoff: Long = IN_MEMORY_CUT_OFF, cacheDirectory: Path? = getTempDirectory()) =
        Resources(this, inMemoryCutoff, cacheDirectory)
}