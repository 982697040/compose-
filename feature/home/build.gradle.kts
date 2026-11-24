// ==============================================================================
// :feature:home 首页业务模块（标准 Feature 模块空壳模板）
// 仅需应用 standard.android.feature 约定插件即可自动接入 Compose、Hilt、Navigation 与核心层。
// 新增其他业务模块时，可直接复制本模块结构并修改 namespace 与包名。
// ==============================================================================

plugins {
    alias(libs.plugins.standard.android.feature)
}

android {
    namespace = "com.bgd.standarddemo.feature.home"
}
