package com.bgd.standarddemo.di

import com.bgd.standarddemo.BuildConfig
import com.bgd.standarddemo.core.common.logger.AppLogger
import com.bgd.standarddemo.core.model.AppConfig
import com.bgd.standarddemo.core.model.AppEnvironment
import com.bgd.standarddemo.core.network.config.NetworkConfig
import com.bgd.standarddemo.core.network.interceptor.TokenProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 壳工程应用级配置注入模块
 *
 * 将 `BuildConfig` 中的编译期环境参数转换为强类型配置对象并注入 Hilt 容器，
 * 使 `:core:*` 与 `:feature:*` 模块无需直接依赖 `:app` 的 `BuildConfig`。
 */
@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    @Provides
    @Singleton
    fun provideAppConfig(): AppConfig {
        val environment = runCatching { AppEnvironment.valueOf(BuildConfig.ENV_NAME) }
            .getOrDefault(AppEnvironment.DEV)

        return AppConfig(
            appName = "StandardDemo",
            versionName = BuildConfig.VERSION_NAME,
            versionCode = BuildConfig.VERSION_CODE,
            isDebug = BuildConfig.DEBUG,
            environment = environment,
            baseUrl = BuildConfig.BASE_URL,
        )
    }

    @Provides
    @Singleton
    fun provideNetworkConfig(appConfig: AppConfig): NetworkConfig {
        return NetworkConfig(
            baseUrl = appConfig.baseUrl,
            isDebug = appConfig.isDebug,
            appVersionName = appConfig.versionName,
        )
    }

    /**
     * 默认空鉴权提供者占位实现
     * 接入实际用户登录模块后，请将本提供方法替换为真实的 `AuthTokenProvider` 绑定。
     */
    @Provides
    @Singleton
    fun provideTokenProvider(): TokenProvider = object : TokenProvider {
        override fun getAccessToken(): String? = null

        override fun onTokenExpired() {
            AppLogger.w("TokenProvider", "收到 401 鉴权失效回调，等待登录模块接管处理")
        }
    }
}
