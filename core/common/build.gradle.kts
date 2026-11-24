// ==============================================================================
// :core:common 通用基础能力模块
// 提供统一结果封装、异常体系、协程调度器注入、日志门面及 MVI/MVVM ViewModel 基类。
// ==============================================================================

plugins {
    alias(libs.plugins.standard.android.library)
    alias(libs.plugins.standard.android.hilt)
}

android {
    namespace = "com.bgd.standarddemo.core.common"
}

dependencies {
    api(projects.core.model)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.android)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.timber)
    implementation(libs.androidx.appcompat)
}
