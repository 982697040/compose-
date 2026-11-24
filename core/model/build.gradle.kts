import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// ==============================================================================
// :core:model 纯 Kotlin 领域模型模块
// 严禁引入任何 Android SDK、Room、Retrofit 等框架依赖，保持领域实体纯净与跨层复用能力。
// ==============================================================================

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
