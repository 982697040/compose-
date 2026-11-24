# 构建、多环境与发布规范（Build & Release Guide）

本文档说明项目的 `build-logic` 约定插件体系、多环境 Flavor 管理、R8 优化与生产发版流程。

---

## 1. `build-logic` 约定插件体系

为了解决传统多模块项目在几十个 `build.gradle.kts` 中复制粘贴配置、难以统一升级的痛点，项目通过复合构建 `build-logic/convention` 提供以下标准插件：

| 插件 ID | 适用模块 | 自动注入的能力 |
| --- | --- | --- |
| `standard.android.application` | `:app` | AGP 9 应用插件、SDK 37/24、Java 11、BuildConfig、基础测试依赖 |
| `standard.android.library` | `:core:*` | AGP 9 库插件、SDK 37/24、Java 11、`consumer-rules.pro`、全套单测依赖 |
| `standard.android.compose` | `:app`, `:core:ui`, `:feature:*` | Compose Compiler 插件、Compose BOM、Material 3、Preview 工具链 |
| `standard.android.hilt` | 需要 DI 的模块 | KSP + Hilt 插件与编译器（无 kapt） |
| `standard.android.room` | `:core:database` | Room Gradle 插件、KSP、自动配置 `schemas/` 导出目录 |
| `standard.android.feature` | 所有 `:feature:*` | 一键组合 Library + Compose + Hilt + Serialization + Navigation + 核心模块依赖 |

> 如需全局升级 `compileSdk`、`minSdk` 或 Java 版本，仅需修改 [`build-logic/convention/src/main/kotlin/com/bgd/standarddemo/buildlogic/ProjectExtensions.kt`](../build-logic/convention/src/main/kotlin/com/bgd/standarddemo/buildlogic/ProjectExtensions.kt) 一处即可生效全项目。

---

## 2. 多环境风味（Product Flavors）

`:app` 模块定义了 `environment` 维度，包含 3 个标准环境：

| Flavor | Application ID | 桌面显示名 | Version Suffix | 默认 Base URL |
| --- | --- | --- | --- | --- |
| `dev` | `com.bgd.standarddemo.dev` | `StandardDemo [DEV]` | `-dev` | `https://dev-api.example.com/` |
| `staging` | `com.bgd.standarddemo.staging` | `StandardDemo [STAGING]` | `-staging` | `https://staging-api.example.com/` |
| `prod` | `com.bgd.standarddemo` | `StandardDemo` | 无 | `https://api.example.com/` |

### 构建变体（Build Variants）选择建议
- **日常开发**：`devDebug`
- **测试提测 / QA 验收**：`stagingDebug` 或 `stagingRelease`（验证 R8 混淆后逻辑无异常）
- **应用商店上架**：`prodRelease`（推荐使用 Android App Bundle：`:app:bundleProdRelease`）

---

## 3. R8 优化与混淆管理（AGP 9）

本项目采用 AGP 9 现代化优化配置：
- `:app` 模块的全局保留规则位于 [`app/src/main/keepRules/rules.keep`](../app/src/main/keepRules/rules.keep)，AGP 9 会自动合并 `src/main/keepRules/` 下的所有规则文件。
- 各 `:core:*` / `:feature:*` 库模块各自的专属保留规则维护在模块根目录的 `consumer-rules.pro` 中。
- **严禁**在库模块的 `consumer-rules.pro` 中写入全局禁用指令（如 `-dontoptimize`、`-dontobfuscate`），AGP 9 会在构建期主动拦截并报错。

---

## 4. 签名配置安全建议（CI / 生产环境）

**严禁将正式发布签名密钥（`.jks` / `.keystore`）和密码硬编码或提交至 Git 仓库。**

推荐在 `app/build.gradle.kts` 中接入签名时，通过环境变量或未入库的 `local.properties` / CI Secrets 注入：
```kotlin
signingConfigs {
    create("release") {
        storeFile = System.getenv("RELEASE_STORE_FILE")?.let(::file)
        storePassword = System.getenv("RELEASE_STORE_PASSWORD")
        keyAlias = System.getenv("RELEASE_KEY_ALIAS")
        keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
    }
}
```

---

## 5. 发版前检查清单（Release Checklist）

- [ ] 确认 `app/build.gradle.kts` 中的 `versionCode` 已递增且 `versionName` 符合语义化版本规范（`MAJOR.MINOR.PATCH`）。
- [ ] 全量单元测试通过：`./gradlew testDevDebugUnitTest :feature:home:testDebugUnitTest`。
- [ ] 使用 `stagingRelease` 构建安装包在真机上完成核心路径回归，确认 R8 混淆未破坏 JSON 序列化与反射调用。
- [ ] 备份本次 `prodRelease` 构建产出的 `mapping.txt` 符号表并上传至线上崩溃监控平台（Crashlytics / Bugly）。
- [ ] 确认 `prod` 环境下未开启明文 HTTP、未打印敏感网络日志。
