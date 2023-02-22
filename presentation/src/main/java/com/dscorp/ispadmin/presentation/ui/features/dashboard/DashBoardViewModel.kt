package com.dscorp.ispadmin.presentation.ui.features.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DashBoardViewModel : ViewModel(), KoinComponent {
    private val repository: IRepository by inject()
    val uiState = MutableLiveData<DashBoardDataUiState>()
    private val userSession = repository.getUserSession()
    fun getDashBoardData() = viewModelScope.launch {

        try {
            val response = repository.getDashBoardData()
            uiState.postValue(DashBoardDataUiState.DashBoardData(response))
        } catch (e: Exception) {
            uiState.postValue(DashBoardDataUiState.DashBoardDataError(e.message ?: ""))
        }

    }

    fun startServiceCut(password: String) = viewModelScope.launch {
        if(password != (userSession?.password ?: "")){
            uiState.postValue(DashBoardDataUiState.CutServiceError("Contraseña incorrecta"))
            return@launch
        }
        try {
            repository.startServicetCut()
            uiState.postValue(DashBoardDataUiState.CutServiceSuccess)
        } catch (e: Exception) {
            uiState.postValue(DashBoardDataUiState.CutServiceError(e.message ?: ""))
        }
    }

}