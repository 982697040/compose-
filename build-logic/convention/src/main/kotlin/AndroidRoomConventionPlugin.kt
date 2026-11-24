import androidx.room.gradle.RoomExtension
import com.bgd.standarddemo.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Room 数据库约定插件
 *
 * 职责：
 * 1. 应用 Room Gradle 插件与 KSP 插件。
 * 2. 自动配置数据库 Schema JSON 导出目录（<module>/schemas），便于版本迁移审计与自动迁移测试。
 * 3. 引入 room-runtime、room-ktx 与基于 KSP 的 room-compiler。
 */
class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("androidx.room")
        pluginManager.apply("com.google.devtools.ksp")

        extensions.configure<RoomExtension> {
            // 将数据库结构版本文件导出至当前模块的 schemas 目录并纳入 Git 版本管理
            schemaDirectory("$projectDir/schemas")
        }

        dependencies {
            add("implementation", libs.findLibrary("room-runtime").get())
            add("implementation", libs.findLibrary("room-ktx").get())
            add("ksp", libs.findLibrary("room-compiler").get())
        }
    }
}
