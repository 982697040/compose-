# StandardDemo — 企业级标准 Android 空项目架构基座

> 基于 **AGP 9 + Gradle 9 + Kotlin 2.4 + Jetpack Compose (Material 3) + Hilt (KSP) + Room + DataStore + Retrofit/OkHttp + 多模块 Clean Architecture + MVI/UDF** 打造的企业级标准 Android 开发脚手架。

---

## 1. 核心技术栈与基线版本

所有依赖版本号均集中维护于 [`gradle/libs.versions.toml`](gradle/libs.versions.toml)，严禁在子模块中硬编码版本号。

| 分类 | 技术选型 | 说明 |
| --- | --- | --- |
| 构建系统 | AGP 9.5.2 / Gradle 9.6.0 / `build-logic` | 内置 Kotlin 支持、配置缓存、类型安全项目访问器、约定插件 |
| 开发语言 | Kotlin 2.4.21 + Coroutines + Flow | 全量 Kotlin 协程与结构化并发 |
| UI 框架 | Jetpack Compose + Material 3 | 声明式 UI、Edge-to-Edge、深浅色主题与动态取色 |
| 架构模式 | Clean Architecture + MVI / UDF | 单向数据流（StateFlow + Channel Effect + Intent） |
| 依赖注入 | Dagger Hilt 2.60.1 (基于 KSP) | 全链路编译期依赖注入 + `@IntoSet` 模块化路由自动聚合 |
| 导航路由 | Jetpack Navigation 3 (1.2.2) | 类型安全 `NavKey`、可控多回退栈 `Navigator`、`NavDisplay` + `SceneStrategy` |
| 网络请求 | Retrofit 3.0 + OkHttp 5.5 + Kotlinx Serialization | 公共请求头（含 `Accept-Language`）、日志脱敏、统一响应解包与异常映射 |
| 本地存储 | Room 2.8.5 (KSP) + DataStore Preferences | 离线优先缓存 + 响应式本地偏好存储 + Schema 自动导出 |
| 多语言（i18n） | Per-App Language (`AppCompat 1.8.0`) + `UiText` | 全版本应用内动态切换语言、自动持久化、模块化中英双语资源 |
| 日志与图片 | Timber + Coil 3 (OkHttp) | 统一日志门面（禁止裸用 `Log.d`）、共享连接池异步图片加载 |
| 单元测试 | JUnit 4 + Coroutines Test + Turbine + MockK | 确定性协程测试与 Flow 状态/事件断言 |

---

## 2. 项目模块拓扑

```text
StandardDemo/
├── build-logic/                 # Gradle 复合构建约定插件（standard.android.*）
│   └── convention/
├── app/                         # 壳工程（Application、MainActivity、全局导航、多环境 Flavor）
├── core/                        # 核心基础设施层（按职责拆分，严禁循环依赖）
│   ├── model/                   # 纯 Kotlin 领域模型层（零 Android 强耦合，含 AppLanguage）
│   ├── common/                  # AppResult、AppException、协程调度器、UiText、AppLocaleManager、BaseViewModel
│   ├── network/                 # Retrofit / OkHttp / 拦截器（含 Accept-Language）/ ApiResponse / SafeApiCall
│   ├── database/                # Room 数据库基座（含 schemas 导出）+ DataStore Preferences
│   ├── data/                    # 离线优先 Repository 仓库接口与默认实现
│   └── ui/                      # Material 3 设计系统、Compose UiText 扩展、通用组件与 Navigation 3 基座
├── feature/                     # 业务功能模块层（横向解耦，Feature 间禁止互相依赖）
│   └── home/                    # 标准业务模块模板（含 MVI 契约、导航、Compose Preview、单测）
├── docs/                        # 企业级详细技术与规范文档
│   ├── ARCHITECTURE.md          # 架构设计与分层依赖规范
│   ├── CODING_STANDARDS.md      # 代码、Compose、协程与 Git 规范
│   ├── MODULE_GUIDE.md          # 新增业务模块 / 接口 / 数据库表操作手册
│   └── BUILD_AND_RELEASE.md     # 多环境构建、混淆与发版指南
└── gradle/
    └── libs.versions.toml       # 全局统一版本目录
```

---

## 3. 快速开始

### 3.1 环境要求
- **Android Studio**：支持 AGP 9.5+ 的最新版本
- **JDK**：JDK 17+（推荐 Android Studio 内置 JBR）
- **Android SDK**：Compile SDK 37 / Min SDK 24

### 3.2 常用 Gradle 命令

```bash
# 1. 构建开发环境 Debug 包（推荐日常开发联调）
./gradlew :app:assembleDevDebug

# 2. 构建预发环境 Debug 包
./gradlew :app:assembleStagingDebug

# 3. 构建正式环境 Release 包（开启 R8 全量优化）
./gradlew :app:assembleProdRelease

# 4. 运行全项目本地单元测试
./gradlew testDevDebugUnitTest :feature:home:testDebugUnitTest

# 5. 检查所有模块编译与配置健康度
./gradlew check
```

> **多环境并存提示**：`dev`（`.dev`）、`staging`（`.staging`）、`prod` 三个构建风味具有不同的 `applicationIdSuffix` 和桌面展示名（`StandardDemo [DEV]` 等），支持在同一台测试机上同时安装并排对比。

---

## 4. 文档导航

新成员入职或开始新业务迭代前，请依次阅读以下详细文档：

1. **[架构设计文档 (`docs/ARCHITECTURE.md`)](docs/ARCHITECTURE.md)**：深入了解分层架构、模块依赖禁令、单向数据流（UDF/MVI）、离线优先数据策略与错误处理体系。
2. **[企业级开发与代码规范 (`docs/CODING_STANDARDS.md`)](docs/CODING_STANDARDS.md)**：涵盖包名命名、Kotlin/Compose 编码最佳实践、协程安全规范、日志与安全红线、Git 工作流。
3. **[业务扩展与模块接入指南 (`docs/MODULE_GUIDE.md`)](docs/MODULE_GUIDE.md)**：手把手演示如何新增 `:feature:xxx` 业务模块、接入新接口、新增 Room 表以及编写 ViewModel 单元测试。
4. **[构建、多环境与发布规范 (`docs/BUILD_AND_RELEASE.md`)](docs/BUILD_AND_RELEASE.md)**：说明约定插件工作原理、Flavor 环境切换、R8 混淆规则维护与正式发版清单。
