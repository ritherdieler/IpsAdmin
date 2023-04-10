package com.dscorp.ispadmin.presentation.ui.features.base

data class BaseUiState<T>(
    val uiState: T? = null,
    val loading: Boolean? = null,
    val error: Exception? = null,
)