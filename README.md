# embed

> 将静态资源直接嵌入到 Kotlin 二进制文件中

**embed** 是一个 Kotlin Multiplatform 资源嵌入库，可将静态资源（文本、JSON、HTML、图片等）在编译时编码为 Base64 分块字符串，直接嵌入到编译产物中，并在运行时通过 `Resources` 类高效访问。

> 本库的设计思路参考（copy）自 [KtEmbed](https://github.com/ktool-dev/ktembed)，在实现上做了简化与调整。

## 特性

- **Kotlin Multiplatform** — 支持 JVM 和 Native（Linux x64/arm64、macOS Arm64、Windows x64）
- **零运行时反射** — 资源编码为静态字符串字面量，编译时确定
- **惰性解码** — 首次访问时才解码，支持内存缓存
- **智能缓存** — 自动磁盘缓存，带 SHA-256 完整性校验
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

ktembed {
    packageName = "com.example.resources"
    resourceDirectories = listOf("src/main/resources")
}
```

### 2. 或者使用代码生成器

```kotlin
val processor = AssetProcessor()
processor.process(
    directories = listOf("src/main/resources".toPath()),
    packageName = "com.example.resources",
    baseOutputDir = outputDir.toPath()
)
```

### 3. 运行时访问

```kotlin
val resources = ResourceDirectory.toResources()

// 读取为字符串
val config = resources.asString("config.json")

// 读取为字节数组
val image = resources.asByteArray("logo.png")

// 检查资源是否存在
if (resources.exists("optional.txt")) {
    println(resources.asString("optional.txt"))
}

// 写入到文件
resources.write("data.bin", sink)

// 获取缓存文件路径
val path = resources.asPath("data.bin")
```

## API

### Resources

| 方法 | 说明 |
|------|------|
| `exists(path)` | 检查资源是否存在 |
| `asString(path)` | 读取为 UTF-8 字符串（内存缓存） |
| `asByteArray(path)` | 读取为字节数组（内存缓存） |
| `asPath(path)` | 提取到缓存目录并返回文件路径 |
| `write(path, sink)` | 写入到 Okio Sink，自动选择优化策略 |
| `write(path, sink, strategy)` | 指定优化策略写入 |

### OptimizationStrategy

- `Speed` — 全量解码到内存后写入，适合小资源
- `Memory` — 从磁盘缓存流式写入，适合大资源

默认 44KB 为分界线，小于等于走 Speed，大于走 Memory。

## 工作原理

1. **编译时** — 扫描资源目录，将文件内容分块（每块 ~32KB Base64），生成 Kotlin 源码文件
2. **编译** — 生成的源码随项目一同编译，资源字符串直接嵌入到编译产物中
3. **运行时** — `Resources` 惰性解码，首次访问后缓存 `ByteString`，支持磁盘缓存与完整性校验

## 对比 KtEmbed

| 特性 | KtEmbed | embed |
|------|---------|-------|
| 编码方式 | Z85 + zip | Base64 |
| 代码生成 | Gradle 插件 | Gradle 插件 + 独立 CLI |
| 运行时 | Okio | Okio |
| 缓存策略 | 内存 + 磁盘 + 哈希校验 | 内存 + 磁盘 + SHA-256 |
| 分块策略 | 自动分块 | 每块 ~32KB Base64 |

## License

MIT