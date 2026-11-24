// ==============================================================================
// build-logic 复合构建 Settings 配置
// 复用主工程的 gradle/libs.versions.toml，确保构建插件与业务模块版本完全一致。
// ==============================================================================

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
