package com.bgd.standarddemo.core.model

import kotlinx.serialization.Serializable

/**
 * 架构演示用领域模型模板
 *
 * 用于贯通展示「Network / Room -> Repository -> ViewModel -> Compose UI」的完整数据链路。
 * 实际业务开发时，请在 `:core:model` 中按业务域新增对应的领域模型，并按需移除或替换本类。
 *
 * @property id 唯一标识
 * @property title 标题
 * @property description 描述说明
 * @property updatedAtEpochMs 最近更新时间戳（毫秒）
 */
@Serializable
data class SampleItem(
    val id: String,
    val title: String,
    val description: String,
    val updatedAtEpochMs: Long = 0L,
)
