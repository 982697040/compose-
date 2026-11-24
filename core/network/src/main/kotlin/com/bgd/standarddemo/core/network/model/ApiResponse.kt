package com.bgd.standarddemo.core.network.model

import com.bgd.standarddemo.core.common.R
import com.bgd.standarddemo.core.common.error.AppException
import com.bgd.standarddemo.core.common.i18n.UiText
import kotlinx.serialization.Serializable

/**
 * 企业级后端标准 JSON 统一响应壳
 *
 * 约定后端接口统一返回形如：
 * ```json
 * {
 *   "code": 0,
 *   "message": "success",
 *   "data": { ... }
 * }
 * ```
 * 若实际后端成功码为 200，可统一调整 [SUCCESS_CODE]。
 *
 * @param T 业务载荷类型
 * @property code 业务状态码（默认 0 代表成功）
 * @property message 服务端提示文案（配合 `Accept-Language` 请求头按语言下发）
 * @property data 业务数据实体（可能为空）
 */
@Serializable
data class ApiResponse<T>(
    val code: Int = SUCCESS_CODE,
    val message: String = "",
    val data: T? = null,
) {
    /** 判断当前响应是否代表业务成功 */
    val isSuccess: Boolean
        get() = code == SUCCESS_CODE

    /**
     * 校验并解包非空业务数据；若业务失败或数据缺失则抛出规范化 [AppException]
     */
    fun requireData(): T {
        if (!isSuccess) {
            if (code == UNAUTHORIZED_CODE) {
                throw AppException.UnauthorizedException(
                    code = code,
                    userMessage = message.ifBlank { "Unauthorized" },
                    uiText = if (message.isNotBlank()) {
                        UiText.DynamicString(message)
                    } else {
                        UiText.StringResource(R.string.error_unauthorized)
                    },
                )
            }
            throw AppException.BusinessException(
                code = code,
                userMessage = message.ifBlank { "Business error ($code)" },
                uiText = if (message.isNotBlank()) {
                    UiText.DynamicString(message)
                } else {
                    UiText.StringResource(R.string.error_business_fallback, code)
                },
            )
        }
        return data ?: throw AppException.BusinessException(
            code = CODE_EMPTY_DATA,
            userMessage = "Response data is null",
            uiText = UiText.StringResource(R.string.error_empty_data),
        )
    }

    companion object {
        const val SUCCESS_CODE = 0
        const val UNAUTHORIZED_CODE = 401
        const val CODE_EMPTY_DATA = -1003
    }
}
