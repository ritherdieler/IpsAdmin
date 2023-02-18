package com.dscorp.ispadmin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch

class ReportsViewModel(private val repository: IRepository) : ViewModel() {

    val uiStateLiveData: MutableLiveData<ReportsUiState> = MutableLiveData()

    fun downloadDebtorsDocument() = viewModelScope.launch {
        try {
           val response = repository.downloadDebtorsDocument()
            uiStateLiveData.postValue(ReportsUiState.DebtorsDocument(response))
        } catch (e: Exception) {
            e.printStackTrace()
            uiStateLiveData.postValue(ReportsUiState.DebtorsDocumentError(e.message))
        }

    }


}