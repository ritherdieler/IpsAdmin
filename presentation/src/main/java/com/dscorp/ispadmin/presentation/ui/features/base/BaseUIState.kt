package com.dscorp.ispadmin.presentation.ui.features.base

sealed interface BaseUIState{
    class Loading(val isLoading:Boolean) : BaseUIState
    object Success : BaseUIState
    data class Error(val message: String?) : BaseUIState
    object idle : BaseUIState
}