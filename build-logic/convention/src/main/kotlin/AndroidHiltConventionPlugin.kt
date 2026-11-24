import com.bgd.standarddemo.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Hilt 依赖注入约定插件（基于 KSP）
 *
 * 职责：
 * 1. 应用 KSP（com.google.devtools.ksp）与 Hilt（com.google.dagger.hilt.android）插件。
 * 2. 自动引入 hilt-android 运行时与基于 KSP 的 hilt-android-compiler，避免使用已被 AGP 9 淘汰的 kapt。
 */
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.google.devtools.ksp")
        pluginManager.apply("com.google.dagger.hilt.android")

        dependencies {
            add("implementation", libs.findLibrary("hilt-android").get())
            add("ksp", libs.findLibrary("hilt-compiler").get())
        }
    }
}
