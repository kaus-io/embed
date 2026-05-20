package com.zxhhyj.embed

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.posix.getenv

/**
 * Native 文件系统
 *
 * Native file system
 */
actual fun getFileSystem(): FileSystem? = FileSystem.SYSTEM

@OptIn(ExperimentalForeignApi::class)
/**
 * Native 临时目录（优先 TMPDIR，回退 TMP/TEMP，最后 /tmp）
 *
 * Native temp directory (TMPDIR first, fallback TMP/TEMP, then /tmp)
 */
actual fun getTempDirectory(): Path? {
    val tmpDir = getenv("TMPDIR")?.toKString()
    if (!tmpDir.isNullOrEmpty()) {
        return tmpDir.toPath()
    }

    val tmp = getenv("TMP")?.toKString()
    if (!tmp.isNullOrEmpty()) {
        return tmp.toPath()
    }

    val temp = getenv("TEMP")?.toKString()
    if (!temp.isNullOrEmpty()) {
        return temp.toPath()
    }

    return "/tmp".toPath()
}