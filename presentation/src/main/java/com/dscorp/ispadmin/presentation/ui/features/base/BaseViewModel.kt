package com.dscorp.ispadmin.presentation.ui.features.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

open class BaseViewModel : ViewModel() {

    private val loadingLiveData = MutableLiveData(false)
    private val errorLiveDate = MutableLiveData<Throwable>()


    fun <T> executeSuspend(block: suspend () -> T) = flow {
        try {
            loadingLiveData.value = true
            emit(block())
        } catch (e: Exception) {
            errorLiveDate.value = e
        } finally {
            loadingLiveData.value = false
        }
    }.flowOn(viewModelScope.coroutineContext)


}

