# embed

> 将静态资源直接嵌入到 Kotlin 二进制文件中

**embed** 是一个 Kotlin Multiplatform 资源嵌入库，可将静态资源（文本、JSON、HTML、图片等）在编译时编码为 Base64 分块字符串，直接嵌入到编译产物中，并在运行时通过 `Resources` 类高效访问。

> 本库的设计思路参考（copy）自 [KtEmbed](https://github.com/ktool-dev/ktembed)，在实现上做了简化与调整。

## 特性

- **Kotlin Multiplatform** — 支持 JVM 和 Native（Linux x64/arm64、macOS Arm64、Windows x64）
- **零运行时反射** — 资源编码为静态字符串字面量，编译时确定
- **惰性解码** — 首次访问时才解码，支持内存缓存
- **磁盘缓存** — 自动缓存到临时目录，带 SHA-256 完整性校验
- **优化策略** — 小资源走内存（Speed），大资源走流式写入（Memory），自动切换

## 模块

| 模块 | 路径 | 说明 |
|------|------|------|
| `runtime` | `libraries/runtime` | 运行时库，提供 `Resources`、`Resource`、`ResourceDirectory` 等 API |
| `generator` | `libraries/generator` | 代码生成器，将资源目录扫描并生成为 Kotlin 源码 |
| `gradle` | `integrations/gradle` | Gradle 插件，自动集成生成步骤到构建流程 |
| `demo` | `libraries/demo` | 示例模块，演示基本用法 |

## 快速开始

### 1. 使用 Gradle 插件

```kotlin
// build.gradle.kts
plugins {
    id("com.zxhhyj.embed") version "1.0.0-alpha1"
}

embed {
    packageName = "com.example.resources"
    resourceDirectories = listOf("src/commonMain/resources")
}
```

执行 Gradle 构建后，插件会在 `build/generated/ktembed` 下生成一个 `ResourceDirectory` 单例对象，包含所有资源的 Base64 编码数据。

### 2. 运行时访问

```kotlin
import com.example.resources.ResourceDirectory

// 直接通过下标访问资源
val config = ResourceDirectory["config.json"]?.asString
val image = ResourceDirectory["logo.png"]?.asByteArray

// 列出所有资源
ResourceDirectory.allPaths.forEach { path ->
    println("$path: ${ResourceDirectory[path]?.asString?.take(64)}")
}
```

## API

### ResourceDirectory（生成的对象）

| 成员 | 说明 |
|------|------|
| `get(path)` / `operator [path]` | 根据路径获取 `Resource?` |
| `key` | 资源目录唯一标识 |
| `allPaths` | 所有可用资源路径 |

生成的 `ResourceDirectory` 是一个单例对象，直接通过下标访问资源：

```kotlin
val resource: Resource? = ResourceDirectory["config.json"]
```

### Resource

| 属性 | 类型 | 说明 |
|------|------|------|
| `key` | `String` | 唯一标识键 |
| `chunks` | `List<String>` | Base64 编码的分块列表 |
| `asString` | `String` | 解码后的 UTF-8 字符串（惰性、缓存） |
| `asByteArray` | `ByteArray` | 解码后的字节数组（惰性、缓存） |

### Resources（高级用法）

需要磁盘缓存、写入到文件等高级功能时使用：

```kotlin
import com.example.resources.ResourceDirectory
import com.zxhhyj.embed.Resources

val resources = Resources(ResourceDirectory)
```

| 方法 | 返回值 | 说明 |
|------|--------|------|
| `exists(path)` | `Boolean` | 检查资源是否存在 |
| `asString(path)` | `String` | 读取为 UTF-8 字符串（内存缓存） |
| `asByteArray(path)` | `ByteArray` | 读取为字节数组（内存缓存） |
| `asPath(path)` | `String?` | 提取到缓存目录并返回文件路径，失败返回 null |
| `write(path, output)` | `Unit` | 写入到 Okio Sink，自动选择优化策略 |
| `write(path, output, strategy)` | `Unit` | 指定优化策略写入 |

**JVM 扩展** (JVM-only):

| 方法 | 说明 |
|------|------|
| `Resources.write(path, OutputStream)` | 写入到 Java `OutputStream` |
| `Resources.write(path, OutputStream, OptimizationStrategy)` | 指定策略写入到 `OutputStream` |

### OptimizationStrategy

- `Speed` — 全量解码到内存后写入，适合小资源
- `Memory` — 从磁盘缓存流式写入，适合大资源

默认约 4.4MB 为分界线，资源估算大小（基于 Base64 分块数量计算）小于等于此值走 Speed，大于走 Memory。

## 工作原理

1. **编译时** — 扫描资源目录，将文件内容分块（每块 ~44KB 原始数据，编码为 Base64），生成 Kotlin 源码（`ResourceChunks1.kt`、`ResourceChunks2.kt`…）和一个 `ResourceDirectory` 单例对象
2. **编译** — 生成的源码随项目一同编译，资源字符串直接嵌入到编译产物中
3. **运行时** — `Resource` 惰性解码，首次访问后缓存 `ByteString`；通过 `Resources` 可额外获得磁盘缓存与完整性校验

## 对比 KtEmbed

| 特性 | KtEmbed | embed |
|------|---------|-------|
| 编码方式 | Z85 + zip | Base64 |
| 代码生成 | Gradle 插件 | Gradle 插件 + 独立 CLI + API |
| 运行时 | Okio | Okio |
| 缓存策略 | 内存 + 磁盘 + 哈希校验 | 内存 + 磁盘 + SHA-256 |
| 分块策略 | 自动分块 | 每块 ~44KB 原始数据 |

## License

MIT