package com.bgd.standarddemo.feature.home

import app.cash.turbine.test
import com.bgd.standarddemo.core.common.i18n.AppLocaleManager
import com.bgd.standarddemo.core.common.i18n.UiText
import com.bgd.standarddemo.core.common.i18n.toUiText
import com.bgd.standarddemo.core.data.repository.UserPreferencesRepository
import com.bgd.standarddemo.core.model.AppConfig
import com.bgd.standarddemo.core.model.AppEnvironment
import com.bgd.standarddemo.core.model.AppLanguage
import com.bgd.standarddemo.core.model.DarkThemeConfig
import com.bgd.standarddemo.core.model.UserPreferencesData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * [HomeViewModel] 单元测试模板
 *
 * 演示企业级 ViewModel 单测最佳实践：
 * 1. 使用 Fake Repository / Fake LocaleManager 替代复杂 Mock，测试更稳定、可读性更高。
 * 2. 使用 `StandardTestDispatcher` 接管 `Dispatchers.Main`，保证协程测试完全确定性。
 * 3. 使用 Turbine 验证 `uiState` 状态变更与基于 [UiText] 的多语言 `uiEffect` 发送。
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeAppConfig = AppConfig(
        appName = "StandardDemo",
        versionName = "1.0.0",
        versionCode = 1,
        isDebug = true,
        environment = AppEnvironment.DEV,
        baseUrl = "https://dev-api.example.com/",
    )

    private lateinit var fakePreferencesRepository: FakeUserPreferencesRepository
    private lateinit var fakeAppLocaleManager: FakeAppLocaleManager
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePreferencesRepository = FakeUserPreferencesRepository()
        fakeAppLocaleManager = FakeAppLocaleManager()
        viewModel = HomeViewModel(
            appConfig = fakeAppConfig,
            userPreferencesRepository = fakePreferencesRepository,
            appLocaleManager = fakeAppLocaleManager,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `切换深色模式意图应更新状态并发送本地化提示副作用`() = runTest {
        viewModel.uiEffect.test {
            viewModel.onIntent(HomeUiIntent.ChangeDarkTheme(DarkThemeConfig.DARK))
            advanceUntilIdle()

            assertEquals(DarkThemeConfig.DARK, viewModel.uiState.value.darkThemeConfig)
            val effect = awaitItem()
            assertTrue(effect is HomeUiEffect.ShowMessage)
            assertEquals(
                UiText.StringResource(R.string.home_msg_theme_changed, DarkThemeConfig.DARK.toUiText()),
                (effect as HomeUiEffect.ShowMessage).message,
            )
        }
    }

    @Test
    fun `切换应用语言意图应更新状态并委托给多语言管理器`() = runTest {
        viewModel.onIntent(HomeUiIntent.ChangeLanguage(AppLanguage.EN))
        advanceUntilIdle()

        assertEquals(AppLanguage.EN, viewModel.uiState.value.currentLanguage)
        assertEquals(AppLanguage.EN, fakeAppLocaleManager.activeLanguage)
    }

    @Test
    fun `执行框架自检意图应发送包含当前环境信息的本地化提示`() = runTest {
        viewModel.uiEffect.test {
            viewModel.onIntent(HomeUiIntent.RunFrameworkCheck)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is HomeUiEffect.ShowMessage)
            assertEquals(
                UiText.StringResource(
                    R.string.home_msg_framework_ready,
                    AppEnvironment.DEV.toUiText(),
                    fakeAppConfig.versionName,
                ),
                (effect as HomeUiEffect.ShowMessage).message,
            )
        }
    }

    /**
     * 内存版 Fake 仓库，避免单元测试依赖真实 Android DataStore 文件系统
     */
    private class FakeUserPreferencesRepository : UserPreferencesRepository {
        private val state = MutableStateFlow(UserPreferencesData())
        override val userPreferences: Flow<UserPreferencesData> = state

        override suspend fun setDarkThemeConfig(config: DarkThemeConfig) {
            state.value = state.value.copy(darkThemeConfig = config)
        }

        override suspend fun setUseDynamicColor(enabled: Boolean) {
            state.value = state.value.copy(useDynamicColor = enabled)
        }
    }

    /**
     * 内存版 Fake 多语言管理器，避免单元测试依赖 Android AppCompat 运行时环境
     */
    private class FakeAppLocaleManager(
        var activeLanguage: AppLanguage = AppLanguage.FOLLOW_SYSTEM,
    ) : AppLocaleManager {
        override fun getCurrentLanguage(): AppLanguage = activeLanguage

        override fun getActiveLanguageTag(): String =
            activeLanguage.languageTag.ifBlank { "en-US" }

        override fun setLanguage(language: AppLanguage) {
            activeLanguage = language
        }
    }
}
