package com.bgd.standarddemo.core.database.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bgd.standarddemo.core.model.SampleItem

/**
 * Room 数据库表实体模板
 *
 * 企业级规范：
 * 1. 数据库表实体（`*Entity`）与领域模型（`:core:model`）分离，避免 Room 注解污染纯领域层。
 * 2. 表名和列名统一采用下划线蛇形命名（snake_case），便于 SQL 审查与迁移维护。
 * 3. 通过扩展函数 `toDomainModel()` / `toEntity()` 显式转换。
 */
@Entity(tableName = "sample_items")
data class SampleEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "updated_at_epoch_ms")
    val updatedAtEpochMs: Long,
)

/** 将数据库实体转换为领域模型 */
fun SampleEntity.toDomainModel(): SampleItem = SampleItem(
    id = id,
    title = title,
    description = description,
    updatedAtEpochMs = updatedAtEpochMs,
)

/** 将领域模型转换为数据库实体 */
fun SampleItem.toEntity(): SampleEntity = SampleEntity(
    id = id,
    title = title,
    description = description,
    updatedAtEpochMs = updatedAtEpochMs,
)
