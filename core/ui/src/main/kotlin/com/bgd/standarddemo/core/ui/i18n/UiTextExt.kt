package com.bgd.standarddemo.core.ui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.bgd.standarddemo.core.common.i18n.UiText

/**
 * 在 Jetpack Compose 中将 [UiText] 解析为当前语言对应的字符串
 *
 * 支持嵌套 [UiText] 格式化参数，并在系统或应用语言切换时随 Configuration 变更自动重组更新。
 */
@Composable
@ReadOnlyComposable
fun UiText.asString(): String = when (this) {
    is UiText.DynamicString -> value
    is UiText.StringResource -> {
        if (args.isEmpty()) {
            stringResource(id = resId)
        } else {
            val resolvedArgs = Array<Any>(args.size) { "" }
            for (index in args.indices) {
                val arg = args[index]
                resolvedArgs[index] = if (arg is UiText) arg.asString() else arg
            }
            stringResource(id = resId, formatArgs = resolvedArgs)
        }
    }
}
