package com.bgd.standarddemo.core.model

/**
 * 应用运行环境枚举
 * 与 app 模块的 Gradle Product Flavors 一一对应。
 */
enum class AppEnvironment(val displayName: String) {
    /** 开发联调环境 */
    DEV("开发环境"),

    /** 预发测试环境 */
    STAGING("预发环境"),

    /** 线上正式环境 */
    PROD("生产环境"),
}

/**
 * 全局应用只读配置模型
 * 由壳工程 `:app` 从 `BuildConfig` 装配并通过 Hilt 注入，供各核心层与业务层安全读取，
 * 避免核心层直接耦合具体 `BuildConfig` 类。
 *
 * @property appName 应用名称
 * @property versionName 展示版本号（如 1.0.0）
 * @property versionCode 内部构建版本号
 * @property isDebug 是否处于 Debug 调试构建
 * @property environment 当前构建风味对应的运行环境
 * @property baseUrl 当前环境对应的服务端接口基础域名
 */
data class AppConfig(
    val appName: String,
    val versionName: String,
    val versionCode: Int,
    val isDebug: Boolean,
    val environment: AppEnvironment,
    val baseUrl: String,
)
