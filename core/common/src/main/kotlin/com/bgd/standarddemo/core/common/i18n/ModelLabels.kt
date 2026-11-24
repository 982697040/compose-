package com.bgd.standarddemo.core.common.i18n

import com.bgd.standarddemo.core.common.R
import com.bgd.standarddemo.core.model.AppEnvironment
import com.bgd.standarddemo.core.model.AppLanguage
import com.bgd.standarddemo.core.model.DarkThemeConfig

/**
 * 领域枚举到多语言 [UiText] 的统一映射扩展
 *
 * 将纯 Kotlin 领域模型（`:core:model`）与 Android 字符串资源解耦，
 * 使 ViewModel 与 Compose UI 均可复用同一套本地化文案映射规则。
 */

/** 将 [AppEnvironment] 转换为本地化展示文案 */
fun AppEnvironment.toUiText(): UiText = when (this) {
    AppEnvironment.DEV -> UiText.StringResource(R.string.env_dev)
    AppEnvironment.STAGING -> UiText.StringResource(R.string.env_staging)
    AppEnvironment.PROD -> UiText.StringResource(R.string.env_prod)
}

/** 将 [DarkThemeConfig] 转换为本地化展示文案 */
fun DarkThemeConfig.toUiText(): UiText = when (this) {
    DarkThemeConfig.FOLLOW_SYSTEM -> UiText.StringResource(R.string.theme_follow_system)
    DarkThemeConfig.LIGHT -> UiText.StringResource(R.string.theme_light)
    DarkThemeConfig.DARK -> UiText.StringResource(R.string.theme_dark)
}

/** 将 [AppLanguage] 转换为本地化展示文案 */
fun AppLanguage.toUiText(): UiText = when (this) {
    AppLanguage.FOLLOW_SYSTEM -> UiText.StringResource(R.string.lang_follow_system)
    AppLanguage.ZH_CN -> UiText.StringResource(R.string.lang_zh_cn)
    AppLanguage.EN -> UiText.StringResource(R.string.lang_en)
}
