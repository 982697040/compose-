package com.bgd.standarddemo.core.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bgd.standarddemo.core.common.error.AppException
import com.bgd.standarddemo.core.common.logger.AppLogger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI 状态标记接口（不可变数据类，代表页面的单一可信状态源）
 */
interface UiState

/**
 * UI 用户意图标记接口（封装页面发起的所有用户交互事件）
 */
interface UiIntent

/**
 * UI 一次性副作用标记接口（如 Toast、Snackbar、路由跳转、弹窗关闭等不适合持久化的事件）
 */
interface UiEffect

/**
 * 企业级 MVI / UDF（单向数据流）通用 ViewModel 基类
 *
 * 设计原则：
 * - [uiState] 使用 [StateFlow] 持有不可变状态，保证配置变更（旋转屏幕等）后状态不丢失。
 * - [uiEffect] 使用缓冲 [Channel] 发送一次性事件，避免屏幕重建时重复触发 Toast 或导航。
 * - 统一入口 [onIntent] 接收来自 Compose 页面的所有用户交互。
 * - 提供 [launchSafely] 统一兜底异常日志与结构化并发取消安全。
 *
 * @param S 页面不可变状态类型
 * @param I 页面用户意图类型
 * @param E 页面一次性副作用类型
 * @param initialState 页面初始状态
 */
abstract class BaseViewModel<S : UiState, I : UiIntent, E : UiEffect>(
    initialState: S,
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)

    /** 对外暴露的只读 UI 状态流 */
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    /** 当前最新状态快照，仅限子类内部便捷读取 */
    protected val currentState: S
        get() = _uiState.value

    private val _uiEffect = Channel<E>(capacity = Channel.BUFFERED)

    /** 对外暴露的一次性副作用流 */
    val uiEffect: Flow<E> = _uiEffect.receiveAsFlow()

    /**
     * 统一分发页面意图，由各业务 ViewModel 覆写处理具体分支
     */
    abstract fun onIntent(intent: I)

    /**
     * 线程安全地原子更新不可变 UI 状态
     */
    protected fun updateState(reducer: S.() -> S) {
        _uiState.update { current -> current.reducer() }
    }

    /**
     * 发送一次性副作用事件到 UI 层
     */
    protected fun sendEffect(effect: E) {
        viewModelScope.launch {
            _uiEffect.send(effect)
        }
    }

    /**
     * 带统一异常捕获与取消透传保护的协程启动器
     *
     * @param dispatcher 可选指定调度器，默认使用 `viewModelScope` 上下文
     * @param onError 异常回调，参数已自动规范化为 [AppException]
     * @param block 协程执行体
     */
    protected fun launchSafely(
        dispatcher: CoroutineDispatcher? = null,
        onError: ((AppException) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit,
    ): Job {
        val context = dispatcher ?: kotlin.coroutines.EmptyCoroutineContext
        return viewModelScope.launch(context) {
            try {
                block()
            } catch (cancel: CancellationException) {
                // 必须重新抛出取消异常，遵守 Kotlin 结构化并发规范
                throw cancel
            } catch (throwable: Throwable) {
                val appException = AppException.from(throwable)
                AppLogger.e(
                    tag = this@BaseViewModel::class.java.simpleName,
                    message = "协程执行异常: code=${appException.code}, msg=${appException.userMessage}",
                    throwable = throwable,
                )
                onError?.invoke(appException)
            }
        }
    }
}
