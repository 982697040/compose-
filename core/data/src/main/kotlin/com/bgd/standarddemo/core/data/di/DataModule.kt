package com.bgd.standarddemo.core.data.di

import com.bgd.standarddemo.core.data.repository.DefaultSampleRepository
import com.bgd.standarddemo.core.data.repository.DefaultUserPreferencesRepository
import com.bgd.standarddemo.core.data.repository.SampleRepository
import com.bgd.standarddemo.core.data.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据层 Hilt 绑定模块
 * 使用 `@Binds` 将 Repository 接口绑定至默认实现，减少生成的工厂类代码体积。
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: DefaultUserPreferencesRepository,
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindSampleRepository(
        impl: DefaultSampleRepository,
    ): SampleRepository
}
