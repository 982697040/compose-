package com.bgd.standarddemo.core.network.api

import com.bgd.standarddemo.core.model.SampleItem
import com.bgd.standarddemo.core.network.model.ApiResponse
import retrofit2.http.GET

/**
 * 系统与示例 Retrofit 接口定义模板
 *
 * 企业级规范：
 * 1. 按业务域拆分独立 `XxxApiService` 接口（例如 `AuthApiService`、`OrderApiService`），避免单文件膨胀。
 * 2. 所有接口方法均声明为 `suspend` 函数，返回 [ApiResponse] 标准响应壳。
 * 3. 数据传输对象（DTO）若与领域模型字段差异较大，应放在 `core:network` 的 `dto` 包下，并在 `core:data` 中转换为领域模型。
 */
interface SystemApiService {

    /**
     * 示例接口：拉取首页演示条目列表
     * 实际开发时请替换为真实服务端路径。
     */
    @GET("api/v1/samples")
    suspend fun getSampleItems(): ApiResponse<List<SampleItem>>
}
