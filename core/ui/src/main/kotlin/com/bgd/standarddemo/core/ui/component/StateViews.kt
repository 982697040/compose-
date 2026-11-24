package com.bgd.standarddemo.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bgd.standarddemo.core.ui.R
import com.bgd.standarddemo.core.ui.theme.LocalAppSpacing

/**
 * 通用居中加载中占位视图
 *
 * @param message 可选加载提示文案（默认读取本地化资源）
 */
@Composable
fun AppLoadingView(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.state_loading),
) {
    val spacing = LocalAppSpacing.current
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator()
            if (message.isNotBlank()) {
                Spacer(modifier = Modifier.height(spacing.md))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * 通用空数据占位视图
 *
 * @param title 空状态主标题（默认读取本地化资源）
 * @param description 空状态补充说明
 * @param actionText 可选操作按钮文案
 * @param onActionClick 操作按钮点击回调
 */
@Composable
fun AppEmptyView(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.state_empty_title),
    description: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    val spacing = LocalAppSpacing.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Inbox,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(spacing.md))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            if (!description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(spacing.xs))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
            if (!actionText.isNullOrBlank() && onActionClick != null) {
                Spacer(modifier = Modifier.height(spacing.lg))
                Button(onClick = onActionClick) {
                    Text(text = actionText)
                }
            }
        }
    }
}

/**
 * 通用错误重试占位视图
 *
 * @param message 面向用户的错误提示文案
 * @param retryText 重试按钮文案（默认读取本地化资源）
 * @param onRetry 点击重试回调
 */
@Composable
fun AppErrorView(
    message: String,
    modifier: Modifier = Modifier,
    retryText: String = stringResource(R.string.state_retry),
    onRetry: (() -> Unit)? = null,
) {
    val spacing = LocalAppSpacing.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error,
            )
            Spacer(modifier = Modifier.height(spacing.md))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            if (onRetry != null) {
                Spacer(modifier = Modifier.height(spacing.lg))
                Button(onClick = onRetry) {
                    Text(text = retryText)
                }
            }
        }
    }
}
