package com.bgd.standarddemo.core.network.di

import com.bgd.standarddemo.core.common.logger.AppLogger
import com.bgd.standarddemo.core.network.api.SystemApiService
import com.bgd.standarddemo.core.network.config.NetworkConfig
import com.bgd.standarddemo.core.network.interceptor.HeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 网络层 Hilt 依赖注入模块
 *
 * 统一装配全局单例 [Json]、[OkHttpClient]、[Retrofit] 及各 `ApiService`。
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val NETWORK_LOG_TAG = "OkHttp"

    /**
     * 提供容错性良好的全局 Kotlinx Serialization [Json] 实例：
     * - `ignoreUnknownKeys = true`：后端新增字段不导致旧版 App 崩溃
     * - `coerceInputValues = true`：空值或非法枚举自动回退默认值
     * - `encodeDefaults = true`：序列化请求体时输出带有默认值的字段
     */
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        networkConfig: NetworkConfig,
        headerInterceptor: HeaderInterceptor,
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            AppLogger.d(NETWORK_LOG_TAG, message)
        }.apply {
            // 仅在 Debug 环境打印完整请求/响应体，避免 Release 环境泄露用户隐私及影响性能
            level = if (networkConfig.isDebug) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            // 对敏感请求头自动脱敏
            redactHeader("Authorization")
            redactHeader("Cookie")
        }

        return OkHttpClient.Builder()
            .connectTimeout(networkConfig.connectTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(networkConfig.readTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(networkConfig.writeTimeoutSeconds, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        networkConfig: NetworkConfig,
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        val contentType = "application/json; charset=UTF-8".toMediaType()
        return Retrofit.Builder()
            .baseUrl(networkConfig.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideSystemApiService(retrofit: Retrofit): SystemApiService {
        return retrofit.create(SystemApiService::class.java)
    }
}
