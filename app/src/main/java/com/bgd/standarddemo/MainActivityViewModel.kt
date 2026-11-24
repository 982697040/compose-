package com.bgd.standarddemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bgd.standarddemo.core.data.repository.UserPreferencesRepository
import com.bgd.standarddemo.core.model.UserPreferencesData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * [MainActivity] 顶层界面状态
 * 用于控制启动页（SplashScreen）保持时机与全局主题配置同步。
 */
sealed interface MainActivityUiState {
    /** 正在从本地 DataStore 读取首帧配置（此时保持系统启动页） */
    data object Loading : MainActivityUiState

    /** 首帧配置就绪，进入主界面 */
    data class Ready(val userPreferences: UserPreferencesData) : MainActivityUiState
}

/**
 * 主 Activity 顶层 ViewModel
 */
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> = userPreferencesRepository.userPreferences
        .map<UserPreferencesData, MainActivityUiState> { prefs ->
            MainActivityUiState.Ready(prefs)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
            initialValue = MainActivityUiState.Loading,
        )
}
