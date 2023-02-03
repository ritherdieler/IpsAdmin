package com.dscorp.ispadmin.presentation.ui.features.subscription.debtors

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DebtorsViewModel : ViewModel(), KoinComponent {
    private val repository: IRepository by inject()
    val uiState = MutableLiveData<DebtorsUiState>()
    init {
        fetchDebtors()
    }

    fun fetchDebtors() = viewModelScope.launch {
        try {
            val response = repository.getDebtors()
            uiState.postValue(DebtorsUiState.GetDebtorsSuccess(response))
        } catch (e: Exception) {
            e.printStackTrace()
            uiState.postValue(DebtorsUiState.GetDebtorsError(e.message))
        }

    }

}