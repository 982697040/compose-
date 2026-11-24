// ==============================================================================
// :app 壳工程模块构建配置
// 职责：应用装配入口、多环境 Flavor 配置、版本管理、混淆优化及聚合业务 Feature 模块。
// 通用编译配置已由 standard.android.* 约定插件自动注入。
// ==============================================================================

plugins {
    alias(libs.plugins.standard.android.application)
    alias(libs.plugins.standard.android.compose)
    alias(libs.plugins.standard.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.bgd.standarddemo"

    defaultConfig {
        applicationId = "com.bgd.standarddemo"
        versionCode = 1
        versionName = "1.0.0"
    }

    // ---- 企业级多环境风味维度（支持同一台手机同时安装 dev / staging / prod）----
    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            manifestPlaceholders["appName"] = "StandardDemo [DEV]"
            buildConfigField("String", "ENV_NAME", "\"DEV\"")
            buildConfigField("String", "BASE_URL", "\"https://dev-api.example.com/\"")
        }
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            manifestPlaceholders["appName"] = "StandardDemo [STAGING]"
            buildConfigField("String", "ENV_NAME", "\"STAGING\"")
            buildConfigField("String", "BASE_URL", "\"https://staging-api.example.com/\"")
        }
        create("prod") {
            dimension = "environment"
            manifestPlaceholders["appName"] = "@string/app_name"
            buildConfigField("String", "ENV_NAME", "\"PROD\"")
            buildConfigField("String", "BASE_URL", "\"https://api.example.com/\"")
        }
    }

    buildTypes {
        debug {
            // Debug 包关闭代码压缩，保证增量构建与断点调试速度
            isDebuggable = true
        }
        release {
            // Release 包开启 AGP 9 推荐的 R8 全量优化（规则文件位于 src/main/keepRules/rules.keep）
            isDebuggable = false
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
}

dependencies {
    // ---- 核心基础设施层 ----
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(projects.core.data)
    implementation(projects.core.ui)

    // ---- 业务功能模块（新增 Feature 模块后在此处注册依赖）----
    implementation(projects.feature.home)

    // ---- 壳工程专属 AndroidX 与 Navigation 3 依赖 ----
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}
