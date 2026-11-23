# WinScript Support

面向 IntelliJ 平台（2025.3+ 统一免费版）的 Windows 脚本支持插件：Batch（`.bat` / `.cmd`）与 PowerShell（`.ps1`）的语法高亮、脚本运行配置与空命令执行，不强制用户安装外部服务。已发布至 [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/35582-winscript-support)（插件 ID 35582，v0.2.0 审核中）。源码托管于 [GitHub](https://github.com/linxunxr/WinScript-Support)。

设计决策与范围见[插件规格设计文档.md](插件规格设计文档.md)。

## 功能

- 语法高亮：关键字、变量（`%VAR%` / `!VAR!` / `$env:PATH`）、字符串与 here-string、数字、类型表达式、标签、注释（含行中 `rem`、`<# #>` 块注释），配色可在 Color Scheme 中自定义
- 运行配置：Batch Script（`cmd /c`）、PowerShell Script（`pwsh` 优先，回退 `powershell.exe`，可选执行策略）、Shell Command（任意命令行）
- 编辑器右键直接运行 `.bat` / `.ps1`
- 参数与工作目录支持 `$ProjectFileDir$`、`$FileDir$` 等宏展开
- 错误标注：未闭合 `%`、引号、括号、块注释、here-string
- 全局设置：解释器路径覆盖、控制台编码（cmd 默认系统代码页 / pwsh 默认 UTF-8）、执行策略默认值

## 开发环境

- JDK 21：由 `build.gradle.kts` 的 toolchain 固定；本机 JDK 路径按需写入 `~/.gradle/gradle.properties` 的 `org.gradle.java.home`（不入仓库）
- Gradle 9.8.2（wrapper 内置，发行版走腾讯镜像）

## 常用命令

```bash
# 若已配置 org.gradle.java.home，可直接运行：
./gradlew test        # 单元测试
./gradlew buildPlugin # 打包到 build/distributions/
./gradlew runIde      # 启动沙箱 IDE 手测
```

Windows 下若未配置 `org.gradle.java.home`，可在命令前加 `JAVA_HOME=/path/to/jdk-21`（Git Bash），或先设置系统环境变量。

## 发布流程（JetBrains Marketplace）

1. `./gradlew buildPlugin` 产出 `build/distributions/winscript-support-<version>.zip`；
2. 首次发布：登录 [plugins.jetbrains.com](https://plugins.jetbrains.com) 手动上传 zip 创建插件页面（描述与标签已在 plugin.xml 内）；
3. 后续版本：Marketplace 要求签名发布——在 JetBrains 账号生成上传 Token 与签名证书，按 IntelliJ Platform Gradle Plugin 文档配置 `publishPlugin` 任务后执行 `./gradlew publishPlugin`；
4. 发布前建议跑一次 `./gradlew verifyPlugin` 校验 API 兼容性（验证目标由 `build.gradle.kts` 的 `pluginVerification.ides` 决定，当前复用构建时已解析的平台，无需额外下载 IDE）。

## 许可证

本项目采用 [Apache License 2.0](LICENSE)。

## 更新记录

| 日期       | 变更内容                                                                                                     |
|------------|--------------------------------------------------------------------------------------------------------------|
| 2026-10-24 | 仓库建立：提交插件规格设计文档 v0.1。                                                                        |
| 2026-10-28 | M1–M3 交付：工程骨架与语言层、三类运行配置与右键运行、宏展开、错误标注、全局设置与配色页；补开发与发布文档。 |
| 2026-10-28 | v0.1.0 发布至 JetBrains Marketplace（插件 ID 35582，待审核后公开）。                                         |
| 2026-11-21 | 开源配置：补 Apache-2.0 LICENSE，移除 README 与构建脚本中的机器相关路径，verifier 改用可移植的平台引用。     |
| 2026-11-23 | v0.2.0 上传重新送审：无功能变更，仅升版本号以触发重新审核；源码仓库已于 11-21 转公开。                       |
