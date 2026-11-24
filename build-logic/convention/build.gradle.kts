import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// ==============================================================================
// build-logic:convention 约定插件模块
// 封装企业级标准 Android / Compose / Hilt / Room / Feature 构建约定，
// 避免在各个业务模块的 build.gradle.kts 中复制粘贴重复配置。
// ==============================================================================

plugins {
    `kotlin-dsl`
}

group = "com.bgd.standarddemo.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "standard.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "standard.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "standard.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "standard.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = "standard.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("androidFeature") {
            id = "standard.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
    }
}
