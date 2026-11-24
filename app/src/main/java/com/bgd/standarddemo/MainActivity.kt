package com.bgd.standarddemo

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bgd.standarddemo.core.model.DarkThemeConfig
import com.bgd.standarddemo.core.ui.navigation.EntryProviderInstaller
import com.bgd.standarddemo.core.ui.theme.StandardDemoTheme
import com.bgd.standarddemo.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 应用唯一宿主 Activity（Single-Activity + Jetpack Compose + Navigation 3 架构）
 *
 * 继承 [AppCompatActivity] 以在 Android 7–17 全版本上完整支持官方逐应用语言切换（Per-App Language）。
 *
 * 职责：
 * 1. 安装并托管 AndroidX [installSplashScreen]，等待首帧用户偏好加载完毕再平滑淡出，消除深色模式闪烁。
 * 2. 开启全面屏 Edge-to-Edge，并根据当前深色主题状态动态适配状态栏与导航栏图标明暗对比度。
 * 3. 通过 Hilt 收集所有 `:feature:*` 模块贡献的 [EntryProviderInstaller]，挂载 [StandardDemoTheme] 与 [AppNavHost]。
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    /** 由所有业务 Feature 模块通过 Hilt `@IntoSet` 自动聚合的 Navigation 3 路由安装器 */
    @Inject
    lateinit var entryProviderInstallers: Set<@JvmSuppressWildcards EntryProviderInstaller>

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

        // 在生命周期 STARTED 状态下收集顶层偏好配置
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { latestState ->
                    uiState = latestState
                }
            }
        }

        // 保持系统启动画面直到首帧本地配置读取完成，防止主题切换闪屏
        splashScreen.setKeepOnScreenCondition {
            uiState is MainActivityUiState.Loading
        }

        enableEdgeToEdge()

        setContent {
            val readyState = uiState as? MainActivityUiState.Ready
            val darkThemeConfig = readyState?.userPreferences?.darkThemeConfig
                ?: DarkThemeConfig.FOLLOW_SYSTEM
            val useDynamicColor = readyState?.userPreferences?.useDynamicColor ?: false

            val isDarkTheme = when (darkThemeConfig) {
                DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                DarkThemeConfig.LIGHT -> false
                DarkThemeConfig.DARK -> true
            }

            // 随主题实时更新 Edge-to-Edge 系统栏图标颜色，保障深浅色模式下的清晰可读性
            DisposableEffect(isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = android.graphics.Color.TRANSPARENT,
                        darkScrim = android.graphics.Color.TRANSPARENT,
                    ) { isDarkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim = LIGHT_SCRIM,
                        darkScrim = DARK_SCRIM,
                    ) { isDarkTheme },
                )
                onDispose {}
            }

            StandardDemoTheme(
                darkThemeConfig = darkThemeConfig,
                dynamicColor = useDynamicColor,
            ) {
                AppNavHost(entryProviderInstallers = entryProviderInstallers)
            }
        }
    }

    private companion object {
        /** 浅色模式下三键导航栏半透明保护遮罩色 */
        val LIGHT_SCRIM = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

        /** 深色模式下三键导航栏半透明保护遮罩色 */
        val DARK_SCRIM = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)
    }
}
