package com.bgd.standarddemo.core.model

/**
 * 应用支持的多语言配置枚举
 *
 * 与 `app/src/main/res/xml/locales_config.xml` 中声明的语言标签保持一一对应。
 *
 * @property languageTag BCP-47 语言标签；空字符串表示跟随系统语言
 */
enum class AppLanguage(val languageTag: String) {
    /** 跟随系统语言设置 */
    FOLLOW_SYSTEM(""),

    /** 简体中文（中国大陆） */
    ZH_CN("zh-CN"),

    /** 英语（默认国际兜底语言） */
    EN("en"),
    ;

    companion object {
        /**
         * 根据 BCP-47 语言标签解析对应 [AppLanguage]
         */
        fun fromLanguageTag(tag: String?): AppLanguage {
            if (tag.isNullOrBlank()) return FOLLOW_SYSTEM
            val normalized = tag.lowercase()
            return when {
                normalized.startsWith("zh") -> ZH_CN
                normalized.startsWith("en") -> EN
                else -> FOLLOW_SYSTEM
            }
        }
    }
}
