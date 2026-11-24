// ==============================================================================
// 根工程构建文件
// 仅声明子模块共享的插件（apply false），不在此处编写业务逻辑或依赖配置。
// 公共模块配置统一收敛至 build-logic/convention 约定插件中。
// ==============================================================================
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.room) apply false
}
