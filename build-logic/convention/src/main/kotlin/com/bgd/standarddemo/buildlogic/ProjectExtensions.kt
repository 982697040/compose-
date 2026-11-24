package com.bgd.standarddemo.buildlogic

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * 项目构建统一常量配置
 * 所有模块共享的 SDK 版本与 Java 编译目标均在此统一维护。
 */
internal object ProjectConfig {
    /** 编译 SDK 版本（对应 Android 17 / API 37） */
    const val COMPILE_SDK = 37

    /** 目标 SDK 版本 */
    const val TARGET_SDK = 37

    /** 最低兼容 SDK 版本（Android 7.0） */
    const val MIN_SDK = 24

    /** Java 编译兼容版本 */
    val JAVA_VERSION = JavaVersion.VERSION_11
}

/**
 * 获取主工程的 libs.versions.toml 版本目录
 */
internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")
