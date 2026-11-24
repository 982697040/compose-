package com.bgd.standarddemo.core.database.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bgd.standarddemo.core.common.logger.AppLogger
import com.bgd.standarddemo.core.model.DarkThemeConfig
import com.bgd.standarddemo.core.model.UserPreferencesData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 基于 Jetpack DataStore Preferences 的用户偏好本地数据源
 *
 * 企业级规范：
 * 1. 使用 DataStore 全面替代传统的 SharedPreferences，避免主线程 I/O 阻塞与 ANR 风险。
 * 2. 读取时捕获 [IOException] 并回退至空配置，防止损坏文件导致应用崩溃。
 * 3. 若需存储敏感凭证（如 Refresh Token），建议结合 Android Keystore 加密后再写入。
 */
@Singleton
class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    /** 响应式暴露用户本地偏好设置 */
    val userPreferences: Flow<UserPreferencesData> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                AppLogger.e(TAG, "读取 DataStore 偏好配置失败，已回退至默认配置", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val themeName = prefs[KEY_DARK_THEME_CONFIG] ?: DarkThemeConfig.FOLLOW_SYSTEM.name
            val darkThemeConfig = runCatching { DarkThemeConfig.valueOf(themeName) }
                .getOrDefault(DarkThemeConfig.FOLLOW_SYSTEM)

            UserPreferencesData(
                darkThemeConfig = darkThemeConfig,
                useDynamicColor = prefs[KEY_USE_DYNAMIC_COLOR] ?: false,
            )
        }

    /** 更新深色模式配置 */
    suspend fun setDarkThemeConfig(config: DarkThemeConfig) {
        dataStore.edit { prefs ->
            prefs[KEY_DARK_THEME_CONFIG] = config.name
        }
    }

    /** 更新是否启用 Material You 动态取色 */
    suspend fun setUseDynamicColor(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_USE_DYNAMIC_COLOR] = enabled
        }
    }

    private companion object {
        const val TAG = "UserPreferencesDS"
        val KEY_DARK_THEME_CONFIG = stringPreferencesKey("dark_theme_config")
        val KEY_USE_DYNAMIC_COLOR = booleanPreferencesKey("use_dynamic_color")
    }
}
