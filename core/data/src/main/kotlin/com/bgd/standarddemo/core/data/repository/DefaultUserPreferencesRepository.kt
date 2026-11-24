package com.bgd.standarddemo.core.data.repository

import com.bgd.standarddemo.core.database.datastore.UserPreferencesDataSource
import com.bgd.standarddemo.core.model.DarkThemeConfig
import com.bgd.standarddemo.core.model.UserPreferencesData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [UserPreferencesRepository] 默认实现
 */
@Singleton
class DefaultUserPreferencesRepository @Inject constructor(
    private val dataSource: UserPreferencesDataSource,
) : UserPreferencesRepository {

    override val userPreferences: Flow<UserPreferencesData> = dataSource.userPreferences

    override suspend fun setDarkThemeConfig(config: DarkThemeConfig) {
        dataSource.setDarkThemeConfig(config)
    }

    override suspend fun setUseDynamicColor(enabled: Boolean) {
        dataSource.setUseDynamicColor(enabled)
    }
}
