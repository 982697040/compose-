package com.bgd.standarddemo.core.network.util

import com.bgd.standarddemo.core.common.R
import com.bgd.standarddemo.core.common.error.AppException
import com.bgd.standarddemo.core.common.i18n.UiText
import com.bgd.standarddemo.core.common.logger.AppLogger
import com.bgd.standarddemo.core.common.result.AppResult
import com.bgd.standarddemo.core.network.model.ApiResponse
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@PublishedApi
internal const val SAFE_API_CALL_TAG = "SafeApiCall"

/**
 * 企业级安全网络请求执行器（直接返回原始类型 [T]）
 *
 * 自动捕获网络超时、无网、HTTP 异常、JSON 反序列化异常等底层错误，
 * 并统一归类转换为携带多语言 [UiText] 的 [AppException] 封装在 [AppResult.Error] 中返回。
 * 严格透传 [CancellationException]，保证协程取消机制正常工作。
 */
suspend inline fun <T> safeApiCall(
    crossinline block: suspend () -> T,
): AppResult<T> {
    return try {
        AppResult.Success(block())
    } catch (cancel: CancellationException) {
        throw cancel
    } catch (throwable: Throwable) {
        val exception = mapNetworkThrowable(throwable)
        AppLogger.e(SAFE_API_CALL_TAG, "网络请求失败: code=${exception.code}, msg=${exception.userMessage}", throwable)
        AppResult.Error(exception)
    }
}

/**
 * 针对返回 [ApiResponse] 标准壳的网络请求安全执行并自动解包 [ApiResponse.data]
 */
suspend inline fun <T> safeApiResponseCall(
    crossinline block: suspend () -> ApiResponse<T>,
): AppResult<T> = safeApiCall {
    block().requireData()
}

/**
 * 将底层网络/序列化异常精准映射为应用层 [AppException]
 */
@PublishedApi
internal fun mapNetworkThrowable(throwable: Throwable): AppException = when (throwable) {
    is AppException -> throwable
    is SocketTimeoutException -> AppException.NetworkException(
        userMessage = "Network request timed out",
        uiText = UiText.StringResource(R.string.error_timeout),
        cause = throwable,
    )
    is UnknownHostException -> AppException.NetworkException(
        userMessage = "Unknown host",
        uiText = UiText.StringResource(R.string.error_unknown_host),
        cause = throwable,
    )
    is IOException -> AppException.NetworkException(
        userMessage = "Network I/O error",
        uiText = UiText.StringResource(R.string.error_network),
        cause = throwable,
    )
    is HttpException -> {
        if (throwable.code() == 401) {
            AppException.UnauthorizedException(cause = throwable)
        } else {
            AppException.HttpStatusException(
                httpCode = throwable.code(),
                cause = throwable,
            )
        }
    }
    is SerializationException -> AppException.StorageException(
        userMessage = "Response serialization failed",
        uiText = UiText.StringResource(R.string.error_serialization),
        cause = throwable,
    )
    else -> AppException.UnknownException(cause = throwable)
}
