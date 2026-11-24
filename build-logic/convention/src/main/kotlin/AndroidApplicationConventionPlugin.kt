import com.android.build.api.dsl.ApplicationExtension
import com.bgd.standarddemo.buildlogic.ProjectConfig
import com.bgd.standarddemo.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Android Application 壳工程基础约定插件
 *
 * 职责：
 * 1. 应用 com.android.application 插件（AGP 9 内置 Kotlin 支持，无需再应用 kotlin-android）。
 * 2. 统一配置 compileSdk / minSdk / targetSdk / Java 11 编译选项。
 * 3. 开启 BuildConfig 生成，支持多环境 Flavor 注入配置字段。
 * 4. 统一引入基础测试依赖。
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")

        extensions.configure<ApplicationExtension> {
            compileSdk {
                version = release(ProjectConfig.COMPILE_SDK)
            }

            defaultConfig {
                minSdk = ProjectConfig.MIN_SDK
                targetSdk = ProjectConfig.TARGET_SDK
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            compileOptions {
                sourceCompatibility = ProjectConfig.JAVA_VERSION
                targetCompatibility = ProjectConfig.JAVA_VERSION
            }

            buildFeatures {
                buildConfig = true
            }

            packaging {
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
        }

        dependencies {
            add("implementation", libs.findLibrary("androidx-core-ktx").get())
            add("testImplementation", libs.findLibrary("junit").get())
            add("androidTestImplementation", libs.findLibrary("androidx-junit").get())
            add("androidTestImplementation", libs.findLibrary("androidx-espresso-core").get())
        }
    }
}
