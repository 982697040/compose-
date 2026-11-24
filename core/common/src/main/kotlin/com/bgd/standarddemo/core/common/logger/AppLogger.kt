package com.bgd.standarddemo.core.common.logger

import timber.log.Timber

/**
 * 企业级统一日志门面（基于 Timber 封装）
 *
 * 规范要求：
 * 1. 全项目禁止直接使用 `android.util.Log` 或 `println`。
 * 2. Debug 构建默认挂载 [Timber.DebugTree] 输出至 Logcat；Release 构建默认关闭普通日志输出，
 *    并可在 [ReleaseReportingTree] 中按需对接 Firebase Crashlytics、Bugly 或自研日志平台。
 * 3. 禁止打印敏感信息（密码、完整 Token、身份证号、银行卡号等）。
 */
object AppLogger {

    /**
     * 在 Application 启动阶段初始化日志系统
     *
     * @param isDebug 当前是否为 Debug 模式
     * @param releaseTree Release 模式下可选的异常/关键日志上报树
     */
    fun init(
        isDebug: Boolean,
        releaseTree: Timber.Tree? = null,
    ) {
        if (Timber.treeCount > 0) return

        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        } else if (releaseTree != null) {
            Timber.plant(releaseTree)
        }
    }

    fun d(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }

    fun i(tag: String, message: String) {
        Timber.tag(tag).i(message)
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Timber.tag(tag).w(throwable, message)
        } else {
            Timber.tag(tag).w(message)
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Timber.tag(tag).e(throwable, message)
        } else {
            Timber.tag(tag).e(message)
        }
    }
}
