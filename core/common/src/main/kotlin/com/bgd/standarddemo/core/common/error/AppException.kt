package com.bgd.standarddemo.core.common.error

import com.bgd.standarddemo.core.common.R
import com.bgd.standarddemo.core.common.i18n.UiText

/**
 * 企业级统一应用异常基类
 *
 * 所有跨层抛出的可预期异常均应转换为本密封体系的子类，
 * 通过 [uiText] 携带支持多语言的本地化提示文案，避免将底层技术堆栈或硬编码语言暴露给终端用户。
 *
 * @property code 错误码（可用于埋点上报、问题定位及特定业务分支处理）
 * @property userMessage 用于日志排查的默认描述信息
 * @property uiText 面向终端用户的多语言友好提示文案（UI 层应优先渲染该字段）
 */
sealed class AppException(
    val code: Int,
    val userMessage: String,
    val uiText: UiText,
    cause: Throwable? = null,
) : Exception(userMessage, cause) {

    /**
     * 网络连接或传输层异常（无网络、DNS 失败、超时、SSL 握手失败等）
     */
    class NetworkException(
        code: Int = CODE_NETWORK_ERROR,
        userMessage: String = "Network connection error",
        uiText: UiText = UiText.StringResource(R.string.error_network),
        cause: Throwable? = null,
    ) : AppException(code, userMessage, uiText, cause)

    /**
     * HTTP 协议层异常（如 404 / 500 / 502 / 503）
     */
    class HttpStatusException(
        val httpCode: Int,
        userMessage: String = "HTTP error ($httpCode)",
        uiText: UiText = UiText.StringResource(R.string.error_http_status, httpCode),
        cause: Throwable? = null,
    ) : AppException(httpCode, userMessage, uiText, cause)

    /**
     * 登录态失效或鉴权失败异常（如 HTTP 401 或业务约定 Token 过期码）
     */
    class UnauthorizedException(
        code: Int = CODE_UNAUTHORIZED,
        userMessage: String = "Unauthorized",
        uiText: UiText = UiText.StringResource(R.string.error_unauthorized),
        cause: Throwable? = null,
    ) : AppException(code, userMessage, uiText, cause)

    /**
     * 服务端返回的业务错误（HTTP 200 但业务 code != 成功码）
     */
    class BusinessException(
        code: Int,
        userMessage: String,
        uiText: UiText = if (userMessage.isNotBlank()) {
            UiText.DynamicString(userMessage)
        } else {
            UiText.StringResource(R.string.error_business_fallback, code)
        },
        cause: Throwable? = null,
    ) : AppException(code, userMessage, uiText, cause)

    /**
     * 本地存储或数据解析异常（Room 读写失败、JSON 序列化不匹配等）
     */
    class StorageException(
        code: Int = CODE_STORAGE_ERROR,
        userMessage: String = "Storage or serialization error",
        uiText: UiText = UiText.StringResource(R.string.error_storage),
        cause: Throwable? = null,
    ) : AppException(code, userMessage, uiText, cause)

    /**
     * 未归类的未知异常兜底
     */
    class UnknownException(
        code: Int = CODE_UNKNOWN,
        userMessage: String = "Unknown error",
        uiText: UiText = UiText.StringResource(R.string.error_unknown),
        cause: Throwable? = null,
    ) : AppException(code, userMessage, uiText, cause)

    companion object {
        const val CODE_UNKNOWN = -1
        const val CODE_NETWORK_ERROR = -1001
        const val CODE_STORAGE_ERROR = -1002
        const val CODE_UNAUTHORIZED = 401

        /**
         * 将任意 [Throwable] 规范化转换为 [AppException]
         */
        fun from(throwable: Throwable): AppException {
            return when (throwable) {
                is AppException -> throwable
                else -> UnknownException(cause = throwable)
            }
        }
    }
}
