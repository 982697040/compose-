// ==============================================================================
// :core:ui 设计系统、通用 UI 组件与 Navigation 3 导航基座模块
// 集中管理 Material 3 主题 Token、无状态基础组件以及多回退栈 NavigationState / Navigator。
// ==============================================================================

plugins {
    alias(libs.plugins.standard.android.library)
    alias(libs.plugins.standard.android.compose)
}

android {
    namespace = "com.bgd.standarddemo.core.ui"
}

dependencies {
    api(projects.core.common)
    implementation(projects.core.model)

    // Navigation 3 导航基座（通过 api 暴露给依赖 :core:ui 的壳工程与业务模块）
    api(libs.androidx.navigation3.runtime)
    api(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.savedstate.compose)

    // Coil 3 图片加载
    api(libs.coil.compose)
    api(libs.coil.network.okhttp)
}
