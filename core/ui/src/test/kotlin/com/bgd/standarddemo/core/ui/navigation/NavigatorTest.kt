package com.bgd.standarddemo.core.ui.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Navigation 3 [Navigator] 多回退栈单元测试
 *
 * 验证企业级核心导航行为：子页面入栈、防重复连点、跨顶级 Tab 切换与状态保留、返回回退首页栈、栈顶替换与回根。
 */
class NavigatorTest {

    private data object HomeKey : NavKey
    private data object ProfileKey : NavKey
    private data class DetailKey(val id: String) : NavKey
    private data object EditKey : NavKey

    private lateinit var navigationState: NavigationState
    private lateinit var navigator: Navigator

    @Before
    fun setUp() {
        navigationState = NavigationState(
            startRoute = HomeKey,
            topLevelRoute = mutableStateOf<NavKey>(HomeKey),
            backStacks = mapOf(
                HomeKey to NavBackStack<NavKey>(HomeKey),
                ProfileKey to NavBackStack<NavKey>(ProfileKey),
            ),
        )
        navigator = Navigator(navigationState)
    }

    @Test
    fun `导航至子页面与防重复入栈应正确生效`() {
        navigator.navigate(DetailKey("1"))
        navigator.navigate(DetailKey("1"))

        assertEquals(listOf<NavKey>(HomeKey, DetailKey("1")), navigationState.backStacks[HomeKey]?.toList())
        assertEquals(DetailKey("1"), navigationState.currentRoute)
    }

    @Test
    fun `切换顶级 Tab 应保留各自独立回退栈且返回时回退至首页栈`() {
        navigator.navigate(DetailKey("1"))
        navigator.navigate(ProfileKey)

        assertEquals(ProfileKey, navigationState.topLevelRoute)
        assertEquals(ProfileKey, navigationState.currentRoute)

        // 在 Profile 栈根页面触发返回，应切回首页栈且保留首页子页面
        navigator.goBack()
        assertEquals(HomeKey, navigationState.topLevelRoute)
        assertEquals(DetailKey("1"), navigationState.currentRoute)
    }

    @Test
    fun `replaceTop 应替换当前栈顶页面而不保留旧栈顶`() {
        navigator.navigate(DetailKey("1"))
        navigator.replaceTop(EditKey)

        assertEquals(listOf<NavKey>(HomeKey, EditKey), navigationState.backStacks[HomeKey]?.toList())
    }

    @Test
    fun `popToTopLevelRoot 应清空当前栈所有子页面并回到根页面`() {
        navigator.navigate(DetailKey("1"))
        navigator.navigate(EditKey)
        navigator.popToTopLevelRoot()

        assertEquals(listOf<NavKey>(HomeKey), navigationState.backStacks[HomeKey]?.toList())
    }
}
