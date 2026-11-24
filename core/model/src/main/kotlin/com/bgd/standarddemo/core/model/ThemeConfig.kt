package com.bgd.standarddemo.core.model

/**
 * 深色主题模式配置
 */
enum class DarkThemeConfig(val label: String) {
    /** 跟随系统自动切换 */
    FOLLOW_SYSTEM("跟随系统"),

    /** 强制浅色主题 */
    LIGHT("浅色模式"),

    /** 强制深色主题 */
    DARK("深色模式"),
}

/**
 * 用户本地偏好配置领域模型
 *
 * @property darkThemeConfig 深色模式策略
 * @property useDynamicColor 是否启用 Android 12+ Material You 动态取色
 */
data class UserPreferencesData(
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val useDynamicColor: Boolean = false,
)
