package com.bgd.standarddemo.core.network.interceptor

/**
 * 鉴权凭证提供者接口
 *
 * 用于解耦 `:core:network` 与具体账号/登录模块。
 * 当项目接入真实登录鉴权后，在认证模块中实现本接口并通过 Hilt 替换默认绑定即可。
 */
interface TokenProvider {

    /**
     * 获取当前有效的访问令牌（Access Token），未登录时返回 null
     */
    fun getAccessToken(): String?

    /**
     * 当收到 401 鉴权失效回调时触发清理或刷新逻辑
     */
    fun onTokenExpired()
}
