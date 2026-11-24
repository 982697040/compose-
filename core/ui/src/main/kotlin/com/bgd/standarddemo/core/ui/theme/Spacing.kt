package com.bgd.standarddemo.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 企业级 4dp 网格间距规范 Token
 *
 * 通过 [LocalAppSpacing] 在任意 Composable 中访问，确保全应用视觉留白一致。
 */
@Immutable
data class AppSpacing(
    /** 极小间距：4dp */
    val xs: Dp = 4.dp,
    /** 小间距：8dp */
    val sm: Dp = 8.dp,
    /** 中等间距：12dp */
    val md: Dp = 12.dp,
    /** 默认页面内边距：16dp */
    val lg: Dp = 16.dp,
    /** 区块间距：24dp */
    val xl: Dp = 24.dp,
    /** 超大间距：32dp */
    val xxl: Dp = 32.dp,
)

/** 全局间距 CompositionLocal */
val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }
