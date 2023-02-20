package com.dscorp.ispadmin.presentation.ui.features.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch

class DashBoardViewModel(private val repository: IRepository) : ViewModel() {


    val uiState = MutableLiveData<DashBoardDataUiState>()

    fun getDashBoardData() = viewModelScope.launch {

        try {
            val response = repository.getDashBoardData()
            uiState.postValue(DashBoardDataUiState.DashBoardData(response))
        } catch (e: Exception) {
            uiState.postValue(DashBoardDataUiState.DashBoardDataError(e))
        }

    }


}