package com.zxhhyj.embed

import okio.Buffer
import okio.ByteString

/**
 * 单个资源
 *
 * A single embedded resource
 * @param key 唯一标识键
 *
 * Unique identifier key
 * @param chunks Base64 编码的分块列表
 *
 * List of Base64-encoded chunks
 */
data class Resource(val key: String, val chunks: List<String>) {
    /**
     * 解码为 UTF-8 字符串（惰性、缓存）
     *
     * Decoded as UTF-8 string (lazy, cached)
     */
    val asString: String by lazy { byteString.utf8() }

    /**
     * 解码为字节数组（惰性、缓存）
     *
     * Decoded as byte array (lazy, cached)
     */
    val asByteArray: ByteArray by lazy { byteString.toByteArray() }

    private val byteString: ByteString by lazy {
        val buffer = Buffer()
        chunks.forEach { buffer.write(it.decodeChunk()) }
        buffer.readByteString()
    }
}