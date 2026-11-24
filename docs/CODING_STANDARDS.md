# 企业级开发与代码规范（Coding Standards）

为保障多人协作下代码库的高可读性、低缺陷率与长期可维护性，所有提交至主干的代码必须遵守本规范。

---

## 1. 命名与包结构规范

### 1.1 模块与包名映射
每个模块的 `namespace` 必须与模块 Gradle 路径严格一致，防止多模块资源或 R 类冲突：
- `:app` -> `com.bgd.standarddemo`
- `:core:<name>` -> `com.bgd.standarddemo.core.<name>`
- `:feature:<name>` -> `com.bgd.standarddemo.feature.<name>`

### 1.2 核心类命名约定

| 组件类型 | 命名格式 | 示例 |
| --- | --- | --- |
| 领域模型（`:core:model`） | 纯名词，不带后缀 | `UserProfile`, `SampleItem` |
| 网络传输模型（`:core:network`） | `*Dto` / `*Request` / `*Response` | `OrderDetailDto`, `LoginRequest` |
| Room 数据库表实体 | `*Entity` | `SampleEntity` |
| Room DAO 接口 | `*Dao` | `SampleDao` |
| 仓库接口 / 默认实现 | `*Repository` / `Default*Repository` | `SampleRepository` / `DefaultSampleRepository` |
| MVI 契约三件套 | `*UiState` / `*UiIntent` / `*UiEffect` | `HomeUiState`, `HomeUiIntent`, `HomeUiEffect` |
| Compose 页面入口 / 无状态视图 | `*Route` / `*Screen` | `HomeRoute`, `HomeScreen` |
| Navigation 3 路由键（`NavKey`） | `*RouteKey` | `HomeRouteKey`, `ProfileRouteKey` |
| Navigation 3 路由注入模块 | `*NavigationModule` | `HomeNavigationModule` |

---

## 2. Jetpack Compose 编写规范

1. **Modifier 参数约定**：
   - 每个对外暴露的可复用 Composable 必须提供带默认值的 `modifier: Modifier = Modifier`，且作为**第一个可选参数**。
   - 仅将 `modifier` 应用于最外层根节点，不要重复传递给多个子节点。
2. **状态收集安全**：
   - 必须使用 `collectAsStateWithLifecycle()`（来自 `lifecycle-runtime-compose`）而非 `collectAsState()`，避免应用退到后台后仍持续消耗资源。
3. **性能优化与稳定性**：
   - 所有 `UiState` 数据类均需标注 `@Immutable`，避免使用 `List`/`Map` 之外的可变集合（如 `MutableList`）作为 State 字段。
   - 禁止在 Composable 函数体中直接执行 I/O、网络请求或创建重型对象；状态记忆使用 `remember` / `rememberSaveable`。
4. **设计系统 Token 约束**：
   - 禁止在业务页面硬编码颜色（如 `Color(0xFF123456)`），统一使用 `MaterialTheme.colorScheme.*`。
   - 禁止随意手写魔数间距与字号，统一使用 `LocalAppSpacing.current` 与 `MaterialTheme.typography.*`。
5. **Edge-to-Edge 适配**：
   - 页面根容器优先使用 `Scaffold` 并正确消费 `innerPadding`，避免内容被状态栏、挖孔屏或底部导航条遮挡。

---

## 3. Kotlin 协程与并发安全规范

1. **禁止使用 `GlobalScope`**：全局长任务必须注入 `@ApplicationScope`；页面级任务必须使用 `viewModelScope` 或 `BaseViewModel.launchSafely`。
2. **禁止吞掉 `CancellationException`**：任何手动 `try-catch (e: Exception)` 捕获协程异常时，必须先判断 `if (e is CancellationException) throw e`。
3. **禁止硬编码调度器**：Repository 与 UseCase 内部切换线程必须使用构造注入的 `@Dispatcher(AppDispatchers.IO)` / `@Dispatcher(AppDispatchers.DEFAULT)`。
4. **主线程安全（Main-Safety）**：所有 `suspend` 函数必须保证在主线程调用安全（耗时操作内部通过 `withContext(ioDispatcher)` 自行切线程）。

---

## 4. 日志、安全与隐私合规红线

1. **统一日志门面**：禁止调用 `android.util.Log.*` 或 `println()`，一律使用 `AppLogger.d/i/w/e(...)`。
2. **敏感信息脱敏**：严禁在日志、崩溃附加信息或埋点中打印明文密码、完整 `Authorization Token`、手机号、身份证号或支付信息。
3. **网络安全**：
   - `AndroidManifest.xml` 默认强制 `android:usesCleartextTraffic="false"`，禁止明文 HTTP 请求。
   - `android:allowBackup="false"` 默认开启，防止 ADB 备份导出本地敏感数据库。
4. **本地存储**：
   - 严禁新增 `SharedPreferences`，统一使用 `DataStore` 或 `Room`。
   - 严禁在生产环境 Room 数据库中使用 `fallbackToDestructiveMigration()`。

---

## 5. 多语言与国际化（i18n）规范

1. **严禁硬编码面向用户的文案**：
   - Compose UI 中所有可见文本及无障碍 `contentDescription` 必须使用 `stringResource(R.string.xxx)` 或 `UiText.asString()`。
   - ViewModel、Repository、Exception 层严禁持有 `Context` 或拼接硬编码语言文案，统一通过 `:core:common` 的 `UiText`（`UiText.StringResource` / `UiText.DynamicString`）传递，在 UI 层按当前语言实时解析。
2. **默认语言与模块化归属**：
   - `res/values/strings.xml` 统一作为**默认国际兜底语言（英文 `en`）**；简体中文置于 `res/values-zh-rCN/strings.xml`。
   - 字符串资源按模块就近归属（通用异常归 `:core:common`，通用组件归 `:core:ui`，业务页面归对应 `:feature:*`），前缀以模块或语义域命名（如 `home_*`、`error_*`、`dialog_*`）。
3. **逐应用语言切换规范**：
   - 应用内切换语言统一通过注入 `AppLocaleManager` 调用 `setLanguage(...)`，严禁手动覆盖 `Configuration.locale` 或私自缓存语言到 `SharedPreferences`。
   - HTTP 请求头由 `HeaderInterceptor` 自动注入 `Accept-Language`，后端接口应据此返回对应语言的业务文案。
4. **语言名称自指原则**：
   - 语言选择器中的具体语种名称应保留母语书写（例如“简体中文”、“English”），避免用户误切陌生语言后无法切回。

---

## 6. 注释规范（中文）

- 所有 `public` 类、接口、核心方法、配置字段及复杂业务分支必须编写清晰的中文 KDoc 注释，说明**设计意图、前置条件及参数含义**，而非简单复述代码字面逻辑。
- 临时待办事项使用标准格式：`// TODO(姓名, YYYY-MM-DD): 具体待处理事项描述`。

---

## 7. Git 分支与提交规范

### 7.1 分支策略
- `main`：线上稳定生产分支，仅接受发版合并，强制保护。
- `develop`：集成分支，对应 `dev` / `staging` 构建。
- `feat/<模块-需求号>`：新功能开发分支。
- `fix/<问题简述>`：缺陷修复分支。

### 7.2 Commit Message 格式（Conventional Commits）
```text
<type>(<scope>): <简明中文描述>

示例：
feat(home): 新增深色主题切换与本地持久化支持
fix(network): 修复 401 响应重复触发 Token 失效回调的问题
refactor(database): 拆分用户偏好 DataStore 数据源
docs(arch): 补充新业务模块接入指南
test(home): 补充 HomeViewModel 主题切换单元测试
chore(deps): 升级 Retrofit 至 3.0.0
```
