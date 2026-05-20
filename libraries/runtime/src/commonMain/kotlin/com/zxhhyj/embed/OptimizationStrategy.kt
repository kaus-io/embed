package com.zxhhyj.embed

/**
 * 资源写入优化策略
 *
 * Resource write optimization strategy
 */
enum class OptimizationStrategy {
    /**
     * 从磁盘缓存流式写入，节省内存
     *
     * Stream from disk cache, lower memory usage
     */
    Memory,

    /**
     * 全量解码到内存后写入，速度更快
     *
     * Load entire resource into memory, faster
     */
    Speed,
}