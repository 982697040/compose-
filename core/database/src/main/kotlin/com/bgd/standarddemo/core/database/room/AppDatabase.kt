package com.bgd.standarddemo.core.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bgd.standarddemo.core.database.room.dao.SampleDao
import com.bgd.standarddemo.core.database.room.entity.SampleEntity

/**
 * 应用主 Room 数据库定义
 *
 * 升级维护规范：
 * 1. `exportSchema = true` 已启用，每次修改表结构必须递增 `version` 并提供显式 `Migration` 或 `AutoMigration`。
 * 2. 严禁在生产环境使用 `fallbackToDestructiveMigration()` 导致用户本地数据丢失。
 * 3. 生成的 Schema JSON 位于 `core/database/schemas/` 目录，需随 PR 一同提交至 Git。
 */
@Database(
    entities = [
        SampleEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sampleDao(): SampleDao

    companion object {
        const val DATABASE_NAME = "standard_demo.db"
    }
}
