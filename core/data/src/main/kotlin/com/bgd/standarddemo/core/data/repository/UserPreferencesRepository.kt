package com.bgd.standarddemo.core.data.repository

import com.bgd.standarddemo.core.model.DarkThemeConfig
import com.bgd.standarddemo.core.model.UserPreferencesData
import kotlinx.coroutines.flow.Flow

/**
 * 用户偏好设置仓库接口
 *
 * 面向领域与 UI 层提供响应式偏好读取和写入能力，隐藏底层 DataStore 实现细节。
 */
interface UserPreferencesRepository {

    /** 持续订阅用户偏好设置变更 */
    val userPreferences: Flow<UserPreferencesData>

    /** 设置深色模式策略 */
    suspend fun setDarkThemeConfig(config: DarkThemeConfig)

    /** 设置是否启用动态取色 */
    suspend fun setUseDynamicColor(enabled: Boolean)
}
