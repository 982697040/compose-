package com.bgd.standarddemo.core.common.dispatcher

import javax.inject.Qualifier

/**
 * 协程调度器注入限定符
 *
 * 企业级规范：严禁在 Repository / ViewModel / DataSource 中硬编码 `Dispatchers.IO` 或 `Dispatchers.Default`，
 * 必须通过 `@Dispatcher(AppDispatchers.IO)` 构造函数注入，以确保单元测试可无缝替换为 `StandardTestDispatcher`。
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: AppDispatchers)

/**
 * 应用内可用的标准协程调度器类型
 */
enum class AppDispatchers {
    /** 磁盘读写、网络 I/O 阻塞型任务 */
    IO,

    /** CPU 密集型计算任务（JSON 解析、排序、加密、Diff 计算等） */
    DEFAULT,

    /** 主线程 UI 渲染与轻量级交互调度 */
    MAIN,
}

/**
 * 进程级全局协程作用域限定符
 * 用于不随页面销毁的后台长任务（例如全局配置同步、日志刷新、缓存清理等）。
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope
