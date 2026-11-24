package com.bgd.standarddemo.feature.home.navigation

import androidx.navigation3.runtime.NavKey
import com.bgd.standarddemo.core.ui.navigation.EntryProviderInstaller
import com.bgd.standarddemo.feature.home.HomeRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

/**
 * 首页 Navigation 3 类型安全路由键（[NavKey]）
 *
 * 企业级规范：
 * 1. 所有页面路由必须实现 [NavKey] 并标注 `@Serializable`，以支持 `rememberNavBackStack` 进程重建持久化。
 * 2. 每个 Feature 模块仅对外暴露路由对象（及后续可选的导航参数模型），通过 Hilt `@IntoSet` 自动向壳工程注册页面渲染入口。
 */
@Serializable
data object HomeRouteKey : NavKey

/**
 * 首页路由 Hilt 自动注册模块
 *
 * 通过 `@IntoSet` 将首页的 [EntryProviderInstaller] 注入到全局路由集合中，
 * 新增其他业务模块时只需参照本模块编写对应 `@Module`，无需修改壳工程 `AppNavHost`。
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object HomeNavigationModule {

    @IntoSet
    @Provides
    fun provideHomeEntryProviderInstaller(): EntryProviderInstaller = { _ ->
        entry<HomeRouteKey> {
            HomeRoute()
        }
    }
}
