package com.bgd.standarddemo.feature.home

import androidx.compose.runtime.Immutable
import com.bgd.standarddemo.core.common.base.UiEffect
import com.bgd.standarddemo.core.common.base.UiIntent
import com.bgd.standarddemo.core.common.base.UiState
import com.bgd.standarddemo.core.common.i18n.UiText
import com.bgd.standarddemo.core.model.AppConfig
import com.bgd.standarddemo.core.model.AppLanguage
import com.bgd.standarddemo.core.model.DarkThemeConfig

/**
 * 首页 MVI 契约定义（状态 State / 意图 Intent / 副作用 Effect）
 *
 * 将页面所有的输入、输出和一次性事件集中在同一契约文件中定义，
 * 便于团队评审接口边界与编写单元测试。
 */

/**
 * 首页不可变 UI 状态
 * 标注 `@Immutable` 帮助 Compose 编译器跳过无效重组，优化渲染性能。
 */
@Immutable
data class HomeUiState(
    val appConfig: AppConfig? = null,
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val useDynamicColor: Boolean = false,
    val currentLanguage: AppLanguage = AppLanguage.FOLLOW_SYSTEM,
    val isRefreshing: Boolean = false,
) : UiState

/**
 * 首页用户交互意图
 */
sealed interface HomeUiIntent : UiIntent {
    /** 切换深色模式策略 */
    data class ChangeDarkTheme(val config: DarkThemeConfig) : HomeUiIntent

    /** 切换是否启用 Material You 动态取色 */
    data class ToggleDynamicColor(val enabled: Boolean) : HomeUiIntent

    /** 切换应用内显示语言 */
    data class ChangeLanguage(val language: AppLanguage) : HomeUiIntent

    /** 演示触发一次框架健康自检反馈 */
    data object RunFrameworkCheck : HomeUiIntent
}

/**
 * 首页一次性副作用事件
 */
sealed interface HomeUiEffect : UiEffect {
    /** 展示底部 Snackbar 本地化提示消息 */
    data class ShowMessage(val message: UiText) : HomeUiEffect
}
