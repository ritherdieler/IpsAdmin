package com.dscorp.ispadmin.presentation.ui.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repository: IRepository) : ViewModel() {

    val uiState = MutableStateFlow<UiState>(UiState.Idle)

    init {
        getUserSession()
    }

    private fun getUserSession() = viewModelScope.launch {
        val response = repository.getUserSession()
        response?.let { uiState.emit(UiState.UserSessionsFound(response)) }
    }


}