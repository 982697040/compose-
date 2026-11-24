# 业务扩展与模块接入指南（Module Guide）

本手册提供标准化操作步骤，帮助研发团队在 5 分钟内按企业规范新增业务模块、服务端接口、本地数据库表与单元测试。

---

## 1. 新增一个业务功能模块（例如 `:feature:profile`）

### 第一步：注册模块
在根目录 [`settings.gradle.kts`](../settings.gradle.kts) 底部追加：
```kotlin
include(":feature:profile")
```

### 第二步：创建模块构建文件与混淆占位文件
创建 `feature/profile/build.gradle.kts`（复用 `standard.android.feature` 约定插件，无需重复写 Compose/Hilt/Navigation 配置）：
```kotlin
plugins {
    alias(libs.plugins.standard.android.feature)
}

android {
    namespace = "com.bgd.standarddemo.feature.profile"
}
```
同时创建空文件 `feature/profile/consumer-rules.pro`（AGP 9 强制要求声明的混淆文件必须存在）。

### 第三步：按 MVI + Navigation 3 模板创建页面文件
在 `feature/profile/src/main/kotlin/com/bgd/standarddemo/feature/profile/` 下创建：
1. `ProfileContract.kt`：定义 `ProfileUiState`、`ProfileUiIntent`、`ProfileUiEffect`。
2. `ProfileViewModel.kt`：继承 `BaseViewModel<ProfileUiState, ProfileUiIntent, ProfileUiEffect>` 并标注 `@HiltViewModel`（若需接收导航参数，使用 `@HiltViewModel(assistedFactory = ...)` + `@AssistedInject`）。
3. `ProfileScreen.kt`：拆分为 `ProfileRoute`（有状态）与 `ProfileScreen`（无状态 + `@Preview`）。
4. `navigation/ProfileNavigation.kt`：定义 `NavKey` 并通过 Hilt `@IntoSet` 自动注册至全局路由表：
   ```kotlin
   @Serializable
   data class ProfileRouteKey(val userId: String) : NavKey

   @Module
   @InstallIn(ActivityRetainedComponent::class)
   object ProfileNavigationModule {

       @IntoSet
       @Provides
       fun provideProfileEntryProviderInstaller(): EntryProviderInstaller = { navigator ->
           entry<ProfileRouteKey> { key ->
               ProfileRoute(
                   userId = key.userId,
                   onNavigateBack = navigator::goBack,
               )
           }
       }
   }
   ```

### 第四步：挂载至壳工程 `:app`
1. 在 `app/build.gradle.kts` 的 `dependencies` 中加入：
   ```kotlin
   implementation(projects.feature.profile)
   ```
2. **无需修改 `AppNavHost.kt`！** Hilt 会自动通过 `@IntoSet` 将 `ProfileNavigationModule` 聚合到全局 `NavDisplay` 中。（仅当该模块需要作为底部导航栏的顶级 Tab 时，才需将其 `NavKey` 加入 `AppNavHost.kt` 的 `topLevelRoutes` 集合中。）

---

## 2. 接入一个新的后端接口

1. **定义领域模型**：在 `:core:model` 中新增业务模型（纯 Kotlin 数据类）。
2. **定义 DTO 与 API 接口**：
   - 若后端 JSON 字段与领域模型差异较大，在 `:core:network` 的 `dto` 包下新增 `@Serializable` DTO 类。
   - 在 `:core:network` 的 `api` 包下按业务域新增或扩充 `XxxApiService` 接口，返回 `ApiResponse<T>`。
   - 在 `NetworkModule` 中通过 `@Provides @Singleton` 暴露该 `ApiService`。
3. **在 `:core:data` 中封装 Repository**：
   - 定义 `XxxRepository` 接口与 `DefaultXxxRepository` 实现类。
   - 在实现中使用 `withContext(ioDispatcher)` + `safeApiResponseCall { ... }` 发起请求并做 DTO -> 领域模型转换。
   - 在 `DataModule` 中使用 `@Binds` 绑定接口与实现。

---

## 3. 新增 Room 数据库表与版本迁移

1. **新增 Entity 与 DAO**：
   - 在 `:core:database` 的 `room/entity/` 下创建 `XxxEntity.kt` 及模型转换扩展函数。
   - 在 `room/dao/` 下创建 `XxxDao.kt`（查询优先返回 `Flow`，写入使用 `@Upsert`）。
2. **注册到 `AppDatabase`**：
   - 将新 Entity 加入 `AppDatabase` 的 `entities = [...]` 数组。
   - **递增 `version` 版本号**（例如 `1 -> 2`）。
   - 在 `DatabaseModule` 中提供对应 DAO，并在 `Room.databaseBuilder(...)` 注册显式 `Migration(1, 2)`。
3. **提交 Schema 文件**：
   - 执行一次编译后，检查 `core/database/schemas/` 下自动生成的最新 JSON Schema 文件，并随代码一同提交至 Git。

---

## 4. 接入真实用户登录 Token 鉴权

当前框架在 `app/.../di/AppConfigModule.kt` 中提供了默认空 `TokenProvider` 占位实现。接入认证体系后：
1. 在 `:core:data`（或独立认证模块）中实现 `TokenProvider` 接口（推荐从内存缓存读取 Token，持久化结合加密存储）。
2. 在 `onTokenExpired()` 中发送全局会话失效事件流（例如通过 `SharedFlow` 通知 `MainActivity` 跳转登录页并清空回退栈）。
3. 删除 `AppConfigModule` 中的占位 `provideTokenProvider()`，改为 Hilt `@Binds` 绑定真实实现。

---

## 5. 扩展一门新语言（例如新增日语 `ja`）

项目采用 Android 官方 **Per-App Language Preferences** 方案（Android 13+ 系统级原生联动，Android 7–12 由 `AppLocalesMetadataHolderService` 自动持久化），新增语言仅需 4 步：

1. **声明系统语言清单**：在 `app/src/main/res/xml/locales_config.xml` 追加 `<locale android:name="ja" />`。
2. **扩充领域枚举**：在 `:core:model` 的 `AppLanguage` 中新增 `JA("ja")`，并在 `fromLanguageTag(...)` 补充匹配分支。
3. **补充多语言资源与标签映射**：
   - 在 `:core:common`、`ModelLabels.kt` 中补充 `AppLanguage.JA.toUiText()` 及对应字符串资源（如 `日本語`）。
   - 在 `:app`、`:core:common`、`:core:ui` 及各 `:feature:*` 模块下新增 `src/main/res/values-ja/strings.xml` 完成翻译。
4. **自动生效**：首页语言切换器（遍历 `AppLanguage.entries`）与 `HeaderInterceptor`（`Accept-Language` 请求头）均自动识别新语种，无需修改业务逻辑。

---

## 6. 编写单元测试标准模板

参考 [`feature/home/src/test/.../HomeViewModelTest.kt`](../feature/home/src/test/kotlin/com/bgd/standarddemo/feature/home/HomeViewModelTest.kt)：
- **优先使用 Fake 替代过度 Mock**：为 Repository、LocaleManager 编写轻量内存版 `Fake*`，减少测试脆弱性。
- **使用 `StandardTestDispatcher`**：在 `@Before` 中通过 `Dispatchers.setMain(testDispatcher)` 接管主调度器，在 `@After` 中调用 `Dispatchers.resetMain()`。
- **使用 Turbine 校验 Flow**：`viewModel.uiEffect.test { ... }` 可精准断言每一次状态或 `UiText` 副作用发射顺序。
