package com.bgd.standarddemo.core.network.interceptor

import com.bgd.standarddemo.core.common.i18n.AppLocaleManager
import com.bgd.standarddemo.core.network.config.NetworkConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 企业级公共请求头拦截器
 *
 * 自动为所有发出的 HTTP 请求附加标准治理头部：
 * - `Accept-Language`: 当前应用生效的 BCP-47 语言标签（自动联动后端国际化文案）
 * - `X-Platform`: 固定标识 `android`
 * - `X-App-Version`: 当前应用版本
 * - `X-Request-Id`: 每次请求生成唯一链路追踪 ID（便于端到端排障）
 * - `Authorization`: 当存在登录凭证时自动注入 `Bearer <token>`
 */
@Singleton
class HeaderInterceptor @Inject constructor(
    private val networkConfig: NetworkConfig,
    private val tokenProvider: TokenProvider,
    private val appLocaleManager: AppLocaleManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Accept", "application/json")
            .header("Accept-Language", appLocaleManager.getActiveLanguageTag())
            .header("X-Platform", "android")
            .header("X-App-Version", networkConfig.appVersionName)
            .header("X-Request-Id", UUID.randomUUID().toString())

        val token = tokenProvider.getAccessToken()
        if (!token.isNullOrBlank() && original.header("Authorization") == null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val response = chain.proceed(requestBuilder.build())

        // 统一感知 HTTP 401 鉴权失效，通知外部鉴权管理器处理登出或无感刷新
        if (response.code == 401) {
            tokenProvider.onTokenExpired()
        }

        return response
    }
}
