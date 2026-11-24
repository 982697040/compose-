// ==============================================================================
// :core:network 网络基础设施模块
// 统一封装 OkHttp、Retrofit、Kotlinx Serialization、公共请求头拦截器及异常映射。
// ==============================================================================

plugins {
    alias(libs.plugins.standard.android.library)
    alias(libs.plugins.standard.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.bgd.standarddemo.core.network"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)

    api(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization)
    api(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    api(libs.kotlinx.serialization.json)
}
