package com.bgd.standarddemo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.bgd.standarddemo.core.ui.navigation.EntryProviderInstaller
import com.bgd.standarddemo.core.ui.navigation.Navigator
import com.bgd.standarddemo.core.ui.navigation.rememberNavigationState
import com.bgd.standarddemo.core.ui.navigation.toEntries
import com.bgd.standarddemo.feature.home.navigation.HomeRouteKey

/**
 * 应用全局 Navigation 3 导航宿主（Single-Activity 架构核心路由器）
 *
 * 企业级设计要点：
 * 1. 通过 [rememberNavigationState] 维护可跨进程重建持久化的多回退栈状态（扩展底部导航栏时仅需补充 `topLevelRoutes`）。
 * 2. 通过 Hilt Multibinding 注入的 [entryProviderInstallers] 自动汇总各 `:feature:*` 模块注册的页面，
 *    新增业务模块无需在此处硬编码注册代码。
 * 3. 默认启用 [DialogSceneStrategy] 支持弹窗型导航目的地，后续可平滑扩展自适应 List-Detail 等 `SceneStrategy`。
 *
 * @param entryProviderInstallers 由各业务模块通过 `@IntoSet` 贡献的路由安装器集合
 * @param modifier 外部修饰符
 */
@Composable
fun AppNavHost(
    entryProviderInstallers: Set<EntryProviderInstaller>,
    modifier: Modifier = Modifier,
) {
    // 若后续接入底部导航栏（如 Home / Message / Profile），在此补充顶级路由集合即可自动获得独立多回退栈
    val topLevelRoutes: Set<NavKey> = remember { setOf(HomeRouteKey) }

    val navigationState = rememberNavigationState(
        startRoute = HomeRouteKey,
        topLevelRoutes = topLevelRoutes,
    )
    val navigator = remember(navigationState) { Navigator(navigationState) }

    val resolvedEntryProvider = remember(entryProviderInstallers, navigator) {
        entryProvider<NavKey> {
            entryProviderInstallers.forEach { installer ->
                installer(navigator)
            }
        }
    }

    val dialogStrategy = remember { DialogSceneStrategy<NavKey>() }

    NavDisplay(
        entries = navigationState.toEntries(resolvedEntryProvider),
        onBack = navigator::goBack,
        sceneStrategies = listOf(dialogStrategy),
        modifier = modifier,
    )
}
