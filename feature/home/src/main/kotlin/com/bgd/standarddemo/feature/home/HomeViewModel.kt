package com.bgd.standarddemo.feature.home

import com.bgd.standarddemo.core.common.base.BaseViewModel
import com.bgd.standarddemo.core.common.i18n.AppLocaleManager
import com.bgd.standarddemo.core.common.i18n.UiText
import com.bgd.standarddemo.core.common.i18n.toUiText
import com.bgd.standarddemo.core.common.logger.AppLogger
import com.bgd.standarddemo.core.data.repository.UserPreferencesRepository
import com.bgd.standarddemo.core.model.AppConfig
import com.bgd.standarddemo.core.model.AppLanguage
import com.bgd.standarddemo.core.model.DarkThemeConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 首页业务 ViewModel（标准 MVI / UDF 实现模板）
 *
 * 演示：
 * 1. 通过 Hilt 构造函数注入全局配置 [AppConfig]、数据仓库 [UserPreferencesRepository] 与多语言管理器 [AppLocaleManager]。
 * 2. 在初始化阶段通过 [launchSafely] 持续监听本地偏好数据流并更新 [uiState]。
 * 3. 所有用户交互统一经由 [onIntent] 分发处理，副作用通过 [UiText] 承载多语言文案并经 [sendEffect] 发送。
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appConfig: AppConfig,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val appLocaleManager: AppLocaleManager,
) : BaseViewModel<HomeUiState, HomeUiIntent, HomeUiEffect>(
    initialState = HomeUiState(
        appConfig = appConfig,
        currentLanguage = appLocaleManager.getCurrentLanguage(),
    ),
) {

    init {
        observeUserPreferences()
    }

    override fun onIntent(intent: HomeUiIntent) {
        when (intent) {
            is HomeUiIntent.ChangeDarkTheme -> updateDarkTheme(intent.config)
            is HomeUiIntent.ToggleDynamicColor -> updateDynamicColor(intent.enabled)
            is HomeUiIntent.ChangeLanguage -> updateLanguage(intent.language)
            HomeUiIntent.RunFrameworkCheck -> runFrameworkCheck()
        }
    }

    private fun observeUserPreferences() {
        launchSafely {
            userPreferencesRepository.userPreferences.collect { prefs ->
                updateState {
                    copy(
                        darkThemeConfig = prefs.darkThemeConfig,
                        useDynamicColor = prefs.useDynamicColor,
                    )
                }
            }
        }
    }

    private fun updateDarkTheme(config: DarkThemeConfig) {
        launchSafely(
            onError = { error -> sendEffect(HomeUiEffect.ShowMessage(error.uiText)) },
        ) {
            userPreferencesRepository.setDarkThemeConfig(config)
            sendEffect(
                HomeUiEffect.ShowMessage(
                    UiText.StringResource(
                        R.string.home_msg_theme_changed,
                        config.toUiText(),
                    ),
                ),
            )
        }
    }

    private fun updateDynamicColor(enabled: Boolean) {
        launchSafely(
            onError = { error -> sendEffect(HomeUiEffect.ShowMessage(error.uiText)) },
        ) {
            userPreferencesRepository.setUseDynamicColor(enabled)
            val resId = if (enabled) {
                R.string.home_msg_dynamic_color_enabled
            } else {
                R.string.home_msg_dynamic_color_disabled
            }
            sendEffect(HomeUiEffect.ShowMessage(UiText.StringResource(resId)))
        }
    }

    private fun updateLanguage(language: AppLanguage) {
        if (currentState.currentLanguage == language) return
        updateState { copy(currentLanguage = language) }
        appLocaleManager.setLanguage(language)
    }

    private fun runFrameworkCheck() {
        AppLogger.i(TAG, "执行框架运行态自检：env=${appConfig.environment}, baseUrl=${appConfig.baseUrl}")
        sendEffect(
            HomeUiEffect.ShowMessage(
                UiText.StringResource(
                    R.string.home_msg_framework_ready,
                    appConfig.environment.toUiText(),
                    appConfig.versionName,
                ),
            ),
        )
    }

    private companion object {
        const val TAG = "HomeViewModel"
    }
}
