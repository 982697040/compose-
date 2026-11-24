// ==============================================================================
// :core:data 数据仓库层模块
// 负责协调远程数据源（:core:network）与本地数据源（:core:database），
// 向业务层（:feature:*）屏蔽数据来源细节，暴露面向领域的 Repository 接口。
// ==============================================================================

plugins {
    alias(libs.plugins.standard.android.library)
    alias(libs.plugins.standard.android.hilt)
}

android {
    namespace = "com.bgd.standarddemo.core.data"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.core.database)
}
