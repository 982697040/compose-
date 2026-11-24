import com.android.build.api.dsl.LibraryExtension
import com.bgd.standarddemo.buildlogic.ProjectConfig
import com.bgd.standarddemo.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Android Library 通用约定插件
 *
 * 职责：
 * 1. 应用 com.android.library 插件（AGP 9 已原生内置 Kotlin 编译支持）。
 * 2. 统一配置 compileSdk / minSdk / Java 11 编译选项。
 * 3. 默认配置基础单元测试与仪表测试依赖，保持各核心模块配置一致。
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.library")

        extensions.configure<LibraryExtension> {
            compileSdk {
                version = release(ProjectConfig.COMPILE_SDK)
            }

            defaultConfig {
                minSdk = ProjectConfig.MIN_SDK
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                consumerProguardFiles("consumer-rules.pro")
            }

            compileOptions {
                sourceCompatibility = ProjectConfig.JAVA_VERSION
                targetCompatibility = ProjectConfig.JAVA_VERSION
            }
        }

        dependencies {
            add("implementation", libs.findLibrary("androidx-core-ktx").get())
            add("testImplementation", libs.findLibrary("junit").get())
            add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
            add("testImplementation", libs.findLibrary("mockk").get())
            add("testImplementation", libs.findLibrary("turbine").get())
        }
    }
}
