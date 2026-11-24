// ==============================================================================
// 项目全局 Settings 配置
// 负责管理插件仓库、依赖仓库、组合构建（build-logic）以及多模块声明。
// ==============================================================================

pluginManagement {
    // 引入内部构建逻辑复合构建，提供 standard.* 约定插件
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    // 禁止子模块私自声明 repositories，确保依赖来源统一可控
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// 启用类型安全的项目模块访问器（例如 projects.core.ui 替代 project(":core:ui")）
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "StandardDemo"

// ---- 壳工程（应用入口）----
include(":app")

// ---- 核心基础设施层（按职责拆分，严禁循环依赖）----
include(":core:model")
include(":core:common")
include(":core:network")
include(":core:database")
include(":core:data")
include(":core:ui")

// ---- 业务功能层（按业务域横向拆分，Feature 之间禁止互相依赖）----
include(":feature:home")
