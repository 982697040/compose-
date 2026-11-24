package com.bgd.standarddemo.core.database.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.bgd.standarddemo.core.common.dispatcher.AppDispatchers
import com.bgd.standarddemo.core.common.dispatcher.Dispatcher
import com.bgd.standarddemo.core.database.room.AppDatabase
import com.bgd.standarddemo.core.database.room.dao.SampleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * 本地持久化层 Hilt 依赖注入模块
 * 负责提供全局单例 [AppDatabase]、各业务 DAO 及 [DataStore]。
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val USER_PREFERENCES_FILE_NAME = "user_preferences"

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = AppDatabase.DATABASE_NAME,
        )
            // 后续升级数据库版本时，请在此处通过 .addMigrations(MIGRATION_1_2, ...) 注册迁移脚本
            .build()
    }

    @Provides
    fun provideSampleDao(database: AppDatabase): SampleDao = database.sampleDao()

    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            scope = CoroutineScope(SupervisorJob() + ioDispatcher),
            produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES_FILE_NAME) },
        )
    }
}
