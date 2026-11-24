// ==============================================================================
// :core:database 本地持久化基础设施模块
// 统一管理 Room 关系型数据库（结构化业务数据缓存）与 DataStore Preferences（轻量级偏好配置）。
// ==============================================================================

plugins {
    alias(libs.plugins.standard.android.library)
    alias(libs.plugins.standard.android.hilt)
    alias(libs.plugins.standard.android.room)
}

android {
    namespace = "com.bgd.standarddemo.core.database"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(libs.androidx.datastore.preferences)
}
