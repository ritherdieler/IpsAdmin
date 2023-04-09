package com.dscorp.ispadmin.presentation.ui.features.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.plan.planlist.BaseUiState
import kotlinx.coroutines.launch

open class BaseViewModel<T> : ViewModel() {
    val uiState = MutableLiveData<BaseUiState<T>>()

    fun executeWithProgress(func: suspend () -> Unit) = viewModelScope.launch {
        try {
            uiState.value = BaseUiState(true)
            func()
        } catch (e: Exception) {
            uiState.value = BaseUiState(error = e)
        } finally {
            uiState.value = BaseUiState(false)
        }
    }
}



