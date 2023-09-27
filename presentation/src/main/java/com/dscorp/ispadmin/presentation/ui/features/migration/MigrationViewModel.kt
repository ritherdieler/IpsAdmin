package com.dscorp.ispadmin.presentation.ui.features.migration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.domain.entity.PlanResponse
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MigrationViewModel(private val repository: IRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<MigrationUiState>(MigrationUiState.Empty)
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.Lazily, MigrationUiState.Empty)

    fun doMigration(subscriptionId: Int, planId: Int) = viewModelScope.launch {

        try {
            _uiState.emit(MigrationUiState.Loading)
            val response = repository.doMigration(subscriptionId, planId)
            _uiState.emit(MigrationUiState.Success(response))
        } catch (e: Exception) {
            _uiState.emit(MigrationUiState.Error)
        }

    }

    fun getPlans() = viewModelScope.launch {
        try {
            _uiState.emit(MigrationUiState.Loading)
            val response = repository.getPlans()
            _uiState.emit(MigrationUiState.SuccessPlans(response))
        } catch (e: Exception) {
            _uiState.emit(MigrationUiState.Error)
        }
    }

}

sealed class MigrationUiState {

    object Empty : MigrationUiState()
    object Loading : MigrationUiState()
    data class Success(val subscriptionResponse: SubscriptionResponse) : MigrationUiState()
    object Error : MigrationUiState()
    data class SuccessPlans(val plans: List<PlanResponse>) : MigrationUiState()
}
