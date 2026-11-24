package com.bgd.standarddemo.feature.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bgd.standarddemo.core.common.i18n.toUiText
import com.bgd.standarddemo.core.model.AppConfig
import com.bgd.standarddemo.core.model.AppEnvironment
import com.bgd.standarddemo.core.model.AppLanguage
import com.bgd.standarddemo.core.model.DarkThemeConfig
import com.bgd.standarddemo.core.ui.component.AppTopBar
import com.bgd.standarddemo.core.ui.i18n.asString
import com.bgd.standarddemo.core.ui.theme.LocalAppSpacing
import com.bgd.standarddemo.core.ui.theme.StandardDemoTheme

/**
 * 首页路由入口 Composable（有状态层）
 *
 * 企业级 Compose 分层规范：
 * - `*Route` 负责获取 ViewModel、按生命周期安全收集 StateFlow（`collectAsStateWithLifecycle`）以及消费一次性 Effect。
 * - `*Screen` 保持纯无状态（Stateless），仅接收不可变 State 与事件回调 Lambda，便于 Preview 与 UI 自动化测试。
 */
@Composable
internal fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is HomeUiEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(message = effect.message.asString(context))
                }
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
    )
}

/**
 * 首页纯 UI 渲染组件（无状态层）
 */
@Composable
internal fun HomeScreen(
    uiState: HomeUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (HomeUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalAppSpacing.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(title = stringResource(R.string.home_top_bar_title))
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            // 1. 当前运行环境与构建信息卡片
            EnvironmentInfoCard(appConfig = uiState.appConfig)

            // 2. 主题切换（演示 DataStore + Repository + StateFlow 响应式闭环）
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    Text(
                        text = stringResource(R.string.home_theme_card_title),
                        style = MaterialTheme.typography.titleMedium,
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(spacing.xs),
                    ) {
                        DarkThemeConfig.entries.forEach { config ->
                            FilterChip(
                                selected = uiState.darkThemeConfig == config,
                                onClick = { onIntent(HomeUiIntent.ChangeDarkTheme(config)) },
                                label = { Text(text = config.toUiText().asString()) },
                            )
                        }
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.home_dynamic_color_title),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                text = stringResource(R.string.home_dynamic_color_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(
                            checked = uiState.useDynamicColor,
                            onCheckedChange = { enabled ->
                                onIntent(HomeUiIntent.ToggleDynamicColor(enabled))
                            },
                        )
                    }
                }
            }

            // 3. 多语言切换卡片（演示 Android 官方 Per-App Language Preferences）
            LanguageSwitchCard(
                currentLanguage = uiState.currentLanguage,
                onSelectLanguage = { language -> onIntent(HomeUiIntent.ChangeLanguage(language)) },
            )

            // 4. 架构分层概览卡片
            ArchitectureOverviewCard()

            // 5. 一次性 Effect 交互示例按钮
            Button(
                onClick = { onIntent(HomeUiIntent.RunFrameworkCheck) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.home_action_run_check))
            }

            Spacer(modifier = Modifier.height(spacing.sm))
        }
    }
}

@Composable
private fun EnvironmentInfoCard(appConfig: AppConfig?) {
    val spacing = LocalAppSpacing.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            Text(
                text = stringResource(R.string.home_env_card_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            if (appConfig != null) {
                InfoRow(
                    label = stringResource(R.string.home_env_label),
                    value = appConfig.environment.toUiText().asString(),
                )
                InfoRow(
                    label = stringResource(R.string.home_version_label),
                    value = "v${appConfig.versionName} (${appConfig.versionCode})",
                )
                InfoRow(
                    label = stringResource(R.string.home_build_type_label),
                    value = stringResource(
                        if (appConfig.isDebug) {
                            R.string.home_build_type_debug
                        } else {
                            R.string.home_build_type_release
                        },
                    ),
                )
                InfoRow(
                    label = stringResource(R.string.home_base_url_label),
                    value = appConfig.baseUrl,
                )
            }
        }
    }
}

@Composable
private fun LanguageSwitchCard(
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
) {
    val spacing = LocalAppSpacing.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Text(
                    text = stringResource(R.string.home_language_card_title),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(R.string.home_language_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                AppLanguage.entries.forEach { language ->
                    FilterChip(
                        selected = currentLanguage == language,
                        onClick = { onSelectLanguage(language) },
                        label = { Text(text = language.toUiText().asString()) },
                    )
                }
            }
        }
    }
}

private data class ModuleOverviewItem(
    val name: String,
    @param:StringRes val descriptionRes: Int,
)

/** 架构模块清单只读常量（使用资源 ID 避免重组重复分配，同时完整支持多语言切换） */
private val ARCHITECTURE_MODULES = listOf(
    ModuleOverviewItem("build-logic", R.string.home_module_build_logic_desc),
    ModuleOverviewItem(":core:model", R.string.home_module_core_model_desc),
    ModuleOverviewItem(":core:common", R.string.home_module_core_common_desc),
    ModuleOverviewItem(":core:network", R.string.home_module_core_network_desc),
    ModuleOverviewItem(":core:database", R.string.home_module_core_database_desc),
    ModuleOverviewItem(":core:data", R.string.home_module_core_data_desc),
    ModuleOverviewItem(":core:ui", R.string.home_module_core_ui_desc),
    ModuleOverviewItem(":feature:home", R.string.home_module_feature_home_desc),
)

@Composable
private fun ArchitectureOverviewCard() {
    val spacing = LocalAppSpacing.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            Text(
                text = stringResource(R.string.home_modules_card_title),
                style = MaterialTheme.typography.titleMedium,
            )
            ARCHITECTURE_MODULES.forEach { (name, descriptionRes) ->
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(descriptionRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    StandardDemoTheme {
        HomeScreen(
            uiState = HomeUiState(
                appConfig = AppConfig(
                    appName = "StandardDemo",
                    versionName = "1.0.0",
                    versionCode = 1,
                    isDebug = true,
                    environment = AppEnvironment.DEV,
                    baseUrl = "https://dev-api.example.com/",
                ),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
        )
    }
}
