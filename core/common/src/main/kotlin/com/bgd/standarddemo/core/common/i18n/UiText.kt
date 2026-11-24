package com.bgd.standarddemo.core.common.i18n

import android.content.Context
import androidx.annotation.StringRes

/**
 * 企业级多语言文本抽象（解耦 ViewModel / Domain 层与 Android [Context]）
 *
 * 设计目的：
 * 1. ViewModel 与异常体系严禁持有 `Context`，亦禁止硬编码中文文案。
 * 2. 客户端自身提示统一使用 [StringResource]，在 UI 渲染时按当前语言实时解析，天然支持应用内动态切换语言。
 * 3. 服务端动态下发的业务提示文案使用 [DynamicString] 原样透传展示。
 */
sealed interface UiText {

    /** 动态文本（例如后端接口返回的业务错误文案或用户输入内容） */
    data class DynamicString(val value: String) : UiText

    /** 本地化字符串资源（支持格式化占位符参数，如 `%1$s`、`%1$d`） */
    data class StringResource(
        @param:StringRes val resId: Int,
        val args: List<Any> = emptyList(),
    ) : UiText {
        constructor(@StringRes resId: Int, vararg args: Any) : this(resId, args.toList())
    }

    /**
     * 在非 Compose 场景（如 Notification、Service、LaunchedEffect Snackbar）中解析为最终字符串
     */
    fun asString(context: Context): String = when (this) {
        is DynamicString -> value
        is StringResource -> {
            if (args.isEmpty()) {
                context.getString(resId)
            } else {
                val resolvedArgs = args.map { arg ->
                    if (arg is UiText) arg.asString(context) else arg
                }
                context.getString(resId, *resolvedArgs.toTypedArray())
            }
        }
    }
}
