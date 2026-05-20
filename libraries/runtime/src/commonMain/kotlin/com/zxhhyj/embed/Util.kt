package com.zxhhyj.embed

import okio.*
import okio.ByteString.Companion.decodeBase64

/**
 * 每个 Base64 分块的字节数
 *
 * Number of bytes per Base64 chunk
 */
const val RESOURCE_CHUNK_SIZE = 44_000L
internal const val IN_MEMORY_CUT_OFF = RESOURCE_CHUNK_SIZE * 100

/**
 * 获取平台文件系统（可能为 null）
 *
 * Get the platform file system (may be null)
 */
expect fun getFileSystem(): FileSystem?
/**
 * 获取平台临时目录（可能为 null）
 *
 * Get the platform temp directory (may be null)
 */
expect fun getTempDirectory(): Path?

/**
 * 计算资源分块的 SHA-256 哈希
 *
 * Compute SHA-256 hash of resource chunks
 */
fun computeHash(chunks: List<String>): String {
    val hashingSink = HashingSink.sha256(blackholeSink())
    hashingSink.buffer().use { buffer ->
        chunks.forEach { it.decodeBase64()?.also(buffer::write) }
    }
    return hashingSink.hash.hex()
}

internal fun computeHash(path: Path, fileSystem: FileSystem): String {
    val hashingSink = HashingSink.sha256(blackholeSink())
    hashingSink.buffer().use { buffer ->
        fileSystem.source(path).buffer().use { buffer.writeAll(it) }
    }
    return hashingSink.hash.hex()
}

/**
 * 解码 Base64 分块为字节数组
 *
 * Decode a Base64 chunk into a byte array
 */
fun String.decodeChunk(): ByteArray = decodeBase64()?.toByteArray()
    ?: error("Invalid base64 chunk")

/**
 * 将 Buffer 内容编码为 Base64 分块
 *
 * Encode Buffer contents as a Base64 chunk
 */
fun Buffer.encodeChunk(): String = readByteString().encodeChunk()

/**
 * 将 ByteString 编码为 Base64 分块
 *
 * Encode ByteString as a Base64 chunk
 */
fun ByteString.encodeChunk() = toByteArray().encodeChunk()

/**
 * 将字节数组编码为 Base64 分块
 *
 * Encode byte array as a Base64 chunk
 */
fun ByteArray.encodeChunk() = Buffer().write(this).readByteString().base64()