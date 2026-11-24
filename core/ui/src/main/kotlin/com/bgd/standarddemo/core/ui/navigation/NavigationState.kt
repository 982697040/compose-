package com.bgd.standarddemo.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer

/**
 * 业务模块路由注册安装器类型别名
 *
 * 每个 `:feature:*` 模块通过 Hilt `@IntoSet` 贡献自己的 [EntryProviderInstaller]，
 * 壳工程 `:app` 自动聚合所有模块的路由表，彻底消除新增模块时在壳工程反复手动登记页面的耦合。
 */
typealias EntryProviderInstaller = EntryProviderScope<NavKey>.(Navigator) -> Unit

/**
 * 创建可跨越配置变更（如旋转屏幕）与进程重建持久化的 Navigation 3 多回退栈状态
 *
 * @param startRoute 应用初始顶级路由（用户按返回键最终从此页面退出应用）
 * @param topLevelRoutes 所有顶级 Tab 路由集合，每个顶级路由均拥有独立回退栈与状态保留
 */
@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>,
): NavigationState {
    require(startRoute in topLevelRoutes) {
        "startRoute($startRoute) 必须包含在 topLevelRoutes($topLevelRoutes) 中"
    }

    val topLevelRoute = rememberSerializable(
        startRoute,
        topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer()),
    ) {
        mutableStateOf(startRoute)
    }

    val backStacks = topLevelRoutes.associateWith { key -> rememberNavBackStack(key) }

    return remember(startRoute, topLevelRoutes) {
        NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks,
        )
    }
}

/**
 * Navigation 3 全局导航状态持有者（单向数据流 State 层）
 *
 * @property startRoute 首页/入口顶级路由
 * @property topLevelRoute 当前激活的顶级路由
 * @property backStacks 每个顶级路由各自维护的独立回退栈映射
 */
class NavigationState(
    val startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    /** 当前处于活跃状态的顶级路由 */
    var topLevelRoute: NavKey by topLevelRoute

    /** 当前活跃回退栈中的全部页面栈（若不在首页栈，则将首页栈垫底以保证返回首页体验） */
    val stacksInUse: List<NavKey>
        get() = if (topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
        }

    /** 当前展示在栈顶的页面路由 */
    val currentRoute: NavKey
        get() = backStacks[topLevelRoute]?.lastOrNull() ?: topLevelRoute
}

/**
 * 将 [NavigationState] 转换为供 `NavDisplay` 渲染的带装饰器 [NavEntry] 列表
 *
 * 内置两个官方核心装饰器：
 * 1. [rememberSaveableStateHolderNavEntryDecorator]：自动保存并恢复各页面 `rememberSaveable` UI 状态。
 * 2. [rememberViewModelStoreNavEntryDecorator]：为每个页面独立分配作用域化 `ViewModelStore`，
 *    页面出栈时自动销毁对应 `ViewModel`，防止内存泄漏。
 */
@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>,
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator(),
        )
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider,
        )
    }

    return stacksInUse
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}
