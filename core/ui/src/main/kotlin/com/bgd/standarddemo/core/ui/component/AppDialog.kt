package com.bgd.standarddemo.core.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bgd.standarddemo.core.ui.R

/**
 * 企业级通用二次确认弹窗
 *
 * @param title 弹窗标题
 * @param message 弹窗正文内容
 * @param confirmText 确认按钮文案（默认读取本地化资源）
 * @param dismissText 取消按钮文案（默认读取本地化资源）
 * @param onConfirm 确认点击回调
 * @param onDismiss 取消或点击外部关闭回调
 */
@Composable
fun AppConfirmDialog(
    title: String,
    message: String,
    confirmText: String = stringResource(R.string.dialog_confirm),
    dismissText: String = stringResource(R.string.dialog_dismiss),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissText)
            }
        },
    )
}
