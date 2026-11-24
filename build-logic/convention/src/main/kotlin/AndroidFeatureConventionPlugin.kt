import com.bgd.standarddemo.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * 业务功能模块（Feature Module）组合约定插件
 *
 * 所有 `:feature:*` 业务模块只需应用本插件即可一键获得：
 * - Android Library 基础配置
 * - Jetpack Compose + Material 3 支持
 * - Hilt 依赖注入 + Hilt Navigation Compose
 * - Kotlinx Serialization（用于类型安全导航路由）
 * - Lifecycle Compose / ViewModel 扩展
 * - 标准公共模块依赖（:core:ui、:core:common、:core:model、:core:data）
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("standard.android.library")
        pluginManager.apply("standard.android.compose")
        pluginManager.apply("standard.android.hilt")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        dependencies {
            // 业务模块默认可访问核心基础设施层，但禁止跨 Feature 互相依赖
            add("implementation", project(":core:model"))
            add("implementation", project(":core:common"))
            add("implementation", project(":core:data"))
            add("implementation", project(":core:ui"))

            // Navigation 3、生命周期与 Hilt-Compose ViewModel 集成
            add("implementation", libs.findLibrary("androidx-navigation3-runtime").get())
            add("implementation", libs.findLibrary("androidx-navigation3-ui").get())
            add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-navigation3").get())
            add("implementation", libs.findLibrary("hilt-lifecycle-viewmodel-compose").get())
            add("implementation", libs.findLibrary("androidx-lifecycle-runtime-compose").get())
            add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
            add("implementation", libs.findLibrary("kotlinx-serialization-json").get())
        }
    }
}
