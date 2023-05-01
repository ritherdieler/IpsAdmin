package com.dscorp.ispadmin.presentation.ui.features.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseViewModel<T> : ViewModel() {
    val uiState = MutableLiveData<BaseUiState<T>>()

    //this method handle the ui loaders automatically
    fun executeWithProgress(
        doFinally: (() -> Unit)? = null,
        onSuccess: (() -> Unit)? = null,
        onError: (() -> Unit)? = null,
        func: suspend (coroutineContext: CoroutineScope) -> Unit
    ) = viewModelScope.launch {
        try {
            uiState.value = BaseUiState(loading = true)
            func(this)
            onSuccess?.invoke()
        } catch (e: Exception) {
            uiState.value = BaseUiState(error = e)
            onError?.invoke()
            e.printStackTrace()
        } finally {
            doFinally?.invoke()
            uiState.value = BaseUiState(loading = false)
        }
    }

    //with this method you should mange ui loaders manually
    fun executeNoProgress(
        doBefore: (() -> Unit)? = null,
        doFinally: (() -> Unit)? = null,
        onSuccess: (() -> Unit)? = null,
        onError: (() -> Unit)? = null,
        func: suspend (coroutineContext: CoroutineScope) -> Unit
    ) = viewModelScope.launch {
        try {
            doBefore?.invoke()
            func(this)
            onSuccess?.invoke()
        } catch (e: Exception) {
            uiState.value = BaseUiState(error = e)
            onError?.invoke()
            e.printStackTrace()
        } finally {
            doFinally?.invoke()
        }
    }
}



