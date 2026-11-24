package com.bgd.standarddemo.core.database.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.bgd.standarddemo.core.database.room.entity.SampleEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO（数据访问对象）标准编写模板
 *
 * 规范要点：
 * - 查询列表优先返回 [Flow]，支持离线优先（Single Source of Truth）响应式自动刷新 UI。
 * - 写入操作优先使用 `@Upsert`（Room 2.5+ 内置插入或更新语义）并标记为 `suspend`。
 */
@Dao
interface SampleDao {

    /** 持续监听所有示例记录（按更新时间倒序） */
    @Query("SELECT * FROM sample_items ORDER BY updated_at_epoch_ms DESC")
    fun observeAll(): Flow<List<SampleEntity>>

    /** 批量插入或更新记录 */
    @Upsert
    suspend fun upsertAll(items: List<SampleEntity>)

    /** 清空表数据 */
    @Query("DELETE FROM sample_items")
    suspend fun clearAll()
}
