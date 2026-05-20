package com.zxhhyj.embed

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.sink
import java.io.OutputStream

/**
 * JVM 文件系统
 *
 * JVM file system
 */
actual fun getFileSystem(): FileSystem? = FileSystem.SYSTEM

/**
 * JVM 临时目录
 *
 * JVM temp directory
 */
actual fun getTempDirectory(): Path? = System.getProperty("java.io.tmpdir")?.toPath()

/**
 * (JVM) 写入资源到 OutputStream（自动选择优化策略）
 *
 * Write resource to an OutputStream (auto-selects strategy)
 */
fun Resources.write(path: String, out: OutputStream) {
    write(path, out.sink())
}

/**
 * (JVM) 写入资源到 OutputStream（指定优化策略）
 *
 * Write resource to an OutputStream (with explicit strategy)
 */
fun Resources.write(path: String, out: OutputStream, strategy: OptimizationStrategy) {
    write(path, out.sink(), strategy)
}