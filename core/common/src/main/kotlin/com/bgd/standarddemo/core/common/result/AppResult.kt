package com.bgd.standarddemo.core.common.result

import com.bgd.standarddemo.core.common.error.AppException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * 企业级统一异步操作结果封装
 *
 * 用于 Repository -> UseCase -> ViewModel 之间的数据与错误传递，
 * 替代裸露的 try-catch 或 Kotlin 标准库 `Result`（可更好支持加载态与结构化 [AppException]）。
 */
sealed interface AppResult<out T> {

    /**
     * 执行成功并携带业务数据
     */
    data class Success<T>(val data: T) : AppResult<T>

    /**
     * 执行失败并携带规范化的 [AppException]
     */
    data class Error(val exception: AppException) : AppResult<Nothing>

    /**
     * 执行中状态（常用于 Flow 响应式数据流首帧通知）
     */
    data object Loading : AppResult<Nothing>
}

/**
 * 对成功结果的数据执行类型映射转换，错误与加载状态原样透传
 */
inline fun <T, R> AppResult<T>.mapData(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Error -> this
    AppResult.Loading -> AppResult.Loading
}

/**
 * 成功分支副作用回调，支持链式调用
 */
inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) {
        action(data)
    }
    return this
}

/**
 * 失败分支副作用回调，支持链式调用
 */
inline fun <T> AppResult<T>.onError(action: (AppException) -> Unit): AppResult<T> {
    if (this is AppResult.Error) {
        action(exception)
    }
    return this
}

/**
 * 成功时返回数据，否则返回 null
 */
fun <T> AppResult<T>.getOrNull(): T? = (this as? AppResult.Success)?.data

/**
 * 将普通数据 [Flow] 转换为带 Loading / Success / Error 状态的 [AppResult] 流。
 * 注意：协程取消异常 [CancellationException] 必须重新抛出，绝不可吞掉，以保证结构化并发正常取消。
 */
fun <T> Flow<T>.asAppResult(): Flow<AppResult<T>> {
    return this
        .map<T, AppResult<T>> { AppResult.Success(it) }
        .onStart { emit(AppResult.Loading) }
        .catch { throwable ->
            if (throwable is CancellationException) throw throwable
            emit(AppResult.Error(AppException.from(throwable)))
        }
}
