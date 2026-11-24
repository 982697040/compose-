package com.bgd.standarddemo.core.data.repository

import com.bgd.standarddemo.core.common.result.AppResult
import com.bgd.standarddemo.core.model.SampleItem
import kotlinx.coroutines.flow.Flow

/**
 * 离线优先（Offline-First / Single Source of Truth）标准仓库接口模板
 *
 * 演示企业级数据层标准模式：
 * - [observeSamples] 始终以本地数据库为单一可信源向 UI 推送数据流；
 * - [syncSamples] 从远程接口拉取最新数据并落库，数据库更新后自动驱动 UI 刷新。
 */
interface SampleRepository {

    /** 监听本地持久化中的示例列表 */
    fun observeSamples(): Flow<List<SampleItem>>

    /** 触发远程同步并更新本地缓存 */
    suspend fun syncSamples(): AppResult<Unit>
}
