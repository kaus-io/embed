# Changelog

## v1.0.0-alpha2

- Gradle 插件：生成的代码同时注册到 `commonMain` / `main` 和 `commonTest` / `test` 源集
- 生成代码的可见性从 `public` 改为 `internal`，仅限模块内使用

## v1.0.0-alpha1

- 初始版本
- 支持 JVM 和 Native（Linux x64/arm64, macOS Arm64, Windows x64）
- 提供 runtime 运行时库与 generator 代码生成器
- 集成 Gradle 插件