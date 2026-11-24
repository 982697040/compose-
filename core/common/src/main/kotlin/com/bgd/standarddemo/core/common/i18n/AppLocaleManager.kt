package com.bgd.standarddemo.core.common.i18n

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.bgd.standarddemo.core.model.AppLanguage
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 应用内多语言切换管理器接口
 *
 * 抽象为接口便于业务 ViewModel、网络拦截器构造函数注入及单元测试替换 Fake 实现。
 */
interface AppLocaleManager {

    /** 获取当前生效的应用语言配置枚举 */
    fun getCurrentLanguage(): AppLanguage

    /** 获取当前生效的 BCP-47 语言标签（例如 `zh-CN` / `en-US`），常用于 HTTP `Accept-Language` 请求头 */
    fun getActiveLanguageTag(): String

    /** 切换应用语言（Android 13+ 联动系统逐应用语言设置，Android 7–12 自动持久化并重建生效） */
    fun setLanguage(language: AppLanguage)
}

/**
 * 基于 AndroidX [AppCompatDelegate.setApplicationLocales] 的官方标准实现
 */
@Singleton
class DefaultAppLocaleManager @Inject constructor() : AppLocaleManager {

    override fun getCurrentLanguage(): AppLanguage {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (locales.isEmpty) {
            return AppLanguage.FOLLOW_SYSTEM
        }
        return AppLanguage.fromLanguageTag(locales.toLanguageTags())
    }

    override fun getActiveLanguageTag(): String {
        val appLocales = AppCompatDelegate.getApplicationLocales()
        val activeLocale = if (!appLocales.isEmpty) appLocales[0] else null
        return (activeLocale ?: Locale.getDefault()).toLanguageTag()
    }

    override fun setLanguage(language: AppLanguage) {
        val targetLocales = if (language == AppLanguage.FOLLOW_SYSTEM) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(language.languageTag)
        }
        AppCompatDelegate.setApplicationLocales(targetLocales)
    }
}
