package com.bgd.standarddemo

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.bgd.standarddemo.core.common.logger.AppLogger
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import javax.inject.Inject

/**
 * 应用程序全局入口 [Application]
 *
 * 企业级冷启动与基础设施初始化规范：
 * 1. `onCreate` 仅执行轻量级且必须同步就绪的基础设施初始化（例如日志框架）。
 * 2. 实现 Coil 3 的 [SingletonImageLoader.Factory]，通过 `dagger.Lazy<OkHttpClient>` 懒注入全局共享网络客户端，
 *    既复用连接池/公共请求头，又避免在冷启动主线程提前初始化 OkHttp。
 * 3. 若涉及隐私合规，采集设备信息类第三方 SDK 必须在用户同意隐私协议后再触发初始化。
 */
@HiltAndroidApp
class StandardApplication : Application(), SingletonImageLoader.Factory {

    /** 使用 Dagger Lazy 延迟创建，避免拖慢冷启动首帧 */
    @Inject
    lateinit var okHttpClient: Lazy<OkHttpClient>

    override fun onCreate() {
        super.onCreate()
        initLogging()
    }

    /**
     * 为全应用 Coil 3 图片加载器复用全局单例 [OkHttpClient]
     */
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory(callFactory = { okHttpClient.get() }))
            }
            .build()
    }

    /**
     * 初始化全局日志门面
     * 如需接入线上崩溃日志上报（Crashlytics / Bugly），可在此传入自定义 Release `Timber.Tree`。
     */
    private fun initLogging() {
        AppLogger.init(isDebug = BuildConfig.DEBUG)
        AppLogger.i(
            TAG,
            "应用启动完成：env=${BuildConfig.ENV_NAME}, version=${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})",
        )
    }

    private companion object {
        const val TAG = "StandardApplication"
    }
}
