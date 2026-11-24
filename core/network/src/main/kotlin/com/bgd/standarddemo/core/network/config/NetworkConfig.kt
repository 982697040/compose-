package com.bgd.standarddemo.core.network.config

/**
 * 网络层运行时配置模型
 *
 * 通过壳工程 `:app` 注入不同构建风味（dev / staging / prod）的配置，
 * 实现 `:core:network` 与具体环境的完全解耦。
 *
 * @property baseUrl 服务端接口根地址（必须以 `/` 结尾）
 * @property isDebug 是否开启网络请求与响应体日志打印
 * @property appVersionName 当前应用版本号（写入公共 Header 便于服务端灰度与问题排查）
 * @property connectTimeoutSeconds 建立连接超时时间（秒）
 * @property readTimeoutSeconds 读取响应超时时间（秒）
 * @property writeTimeoutSeconds 写入请求超时时间（秒）
 */
data class NetworkConfig(
    val baseUrl: String,
    val isDebug: Boolean,
    val appVersionName: String,
    val connectTimeoutSeconds: Long = 15L,
    val readTimeoutSeconds: Long = 20L,
    val writeTimeoutSeconds: Long = 20L,
)
