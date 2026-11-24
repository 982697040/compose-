package com.bgd.standarddemo.core.ui.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Navigation 3 统一导航调度器（单向数据流 Action 层）
 *
 * 封装前进跳转、多回退栈切换、返回出栈、栈顶替换与清栈重入等企业级常用导航语义，
 * 所有状态变更均作用于 [state]，并由 `NavDisplay` 自动响应渲染。
 */
class Navigator(val state: NavigationState) {

    /**
     * 跳转到指定路由：
     * - 若目标属于顶级路由（如底部 Tab），直接切换当前顶级栈，保留原栈历史。
     * - 否则将其压入当前顶级栈顶（默认忽略与当前栈顶完全相同的重复连续点击）。
     */
    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            state.topLevelRoute = route
        } else {
            val currentStack = state.backStacks[state.topLevelRoute]
                ?: error("未找到顶级路由 ${state.topLevelRoute} 对应的回退栈")
            if (currentStack.lastOrNull() != route) {
                currentStack.add(route)
            }
        }
    }

    /**
     * 执行返回操作：
     * - 若当前位于非首页顶级栈的根页面，则切回首页栈（[NavigationState.startRoute]）。
     * - 否则弹出当前栈顶页面。
     */
    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("未找到顶级路由 ${state.topLevelRoute} 对应的回退栈")
        val currentRoute = currentStack.lastOrNull()

        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }

    /**
     * 替换当前栈顶页面（常用于引导页/中间鉴权页完成后直接替换目标页，防止按返回键回到中间页）
     */
    fun replaceTop(route: NavKey) {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("未找到顶级路由 ${state.topLevelRoute} 对应的回退栈")
        if (currentStack.size > 1) {
            currentStack.removeLastOrNull()
        }
        currentStack.add(route)
    }

    /**
     * 回到当前顶级栈的根页面（常用于再次点击已选中的底部 Tab 快速回顶）
     */
    fun popToTopLevelRoot() {
        val currentStack = state.backStacks[state.topLevelRoute] ?: return
        while (currentStack.size > 1) {
            currentStack.removeLastOrNull()
        }
    }
}
