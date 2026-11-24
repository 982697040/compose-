package com.bgd.standarddemo.core.data.repository

import com.bgd.standarddemo.core.common.dispatcher.AppDispatchers
import com.bgd.standarddemo.core.common.dispatcher.Dispatcher
import com.bgd.standarddemo.core.common.result.AppResult
import com.bgd.standarddemo.core.common.result.mapData
import com.bgd.standarddemo.core.database.room.dao.SampleDao
import com.bgd.standarddemo.core.database.room.entity.toDomainModel
import com.bgd.standarddemo.core.database.room.entity.toEntity
import com.bgd.standarddemo.core.model.SampleItem
import com.bgd.standarddemo.core.network.api.SystemApiService
import com.bgd.standarddemo.core.network.util.safeApiResponseCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [SampleRepository] 默认离线优先实现模板
 *
 * 展示了以下企业级最佳实践：
 * 1. 通过构造函数注入 `@Dispatcher(AppDispatchers.IO)`，确保主线程安全与可测试性。
 * 2. 使用 [safeApiResponseCall] 统一捕获并转换网络/业务异常。
 * 3. 拉取成功后写入 Room，由 [observeSamples] 自动推送给订阅者。
 */
@Singleton
class DefaultSampleRepository @Inject constructor(
    private val apiService: SystemApiService,
    private val sampleDao: SampleDao,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : SampleRepository {

    override fun observeSamples(): Flow<List<SampleItem>> {
        return sampleDao.observeAll().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun syncSamples(): AppResult<Unit> = withContext(ioDispatcher) {
        val remoteResult = safeApiResponseCall {
            apiService.getSampleItems()
        }
        when (remoteResult) {
            is AppResult.Success -> {
                sampleDao.upsertAll(remoteResult.data.map { it.toEntity() })
                remoteResult.mapData { }
            }
            is AppResult.Error -> remoteResult
            AppResult.Loading -> AppResult.Loading
        }
    }
}
