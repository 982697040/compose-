package com.bgd.standarddemo.core.common.di

import com.bgd.standarddemo.core.common.i18n.AppLocaleManager
import com.bgd.standarddemo.core.common.i18n.DefaultAppLocaleManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * `:core:common` 接口绑定模块
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CommonBindingsModule {

    @Binds
    @Singleton
    abstract fun bindAppLocaleManager(impl: DefaultAppLocaleManager): AppLocaleManager
}
