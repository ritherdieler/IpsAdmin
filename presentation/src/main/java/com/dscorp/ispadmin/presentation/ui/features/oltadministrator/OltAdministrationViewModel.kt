package com.dscorp.ispadmin.presentation.ui.features.oltadministrator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.dscorp.ispadmin.presentation.ui.features.oltadministrator.OltAdministrationUiState.*
import com.example.data2.data.repository.IRepository
import com.example.data2.data.response.AdministrativeOnuResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OltAdministrationViewModel(private val repository: IRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<OltAdministrationUiState>(Empty)
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.Lazily, Empty)
    fun getOnuBySn(onuSn: String) = viewModelScope.launch {
        try {
            _uiState.value = Loading
            val response = repository.getOnuBySn(onuSn)
            _uiState.value = GetOnuSuccess(response)
        } catch (e: Exception) {
            _uiState.value = Error(e.message ?: "Error")
        }
    }

    fun showForm() {
        _uiState.value = Empty
    }

    fun deleteOnuFromOlt(onuExternalId:String) =viewModelScope.launch{
        try {
            _uiState.value = Loading
            repository.deleteOnuFromOlt(onuExternalId)
            _uiState.value = DeleteOnuSuccess
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.value = Error(e.message ?: "Error")
        }
    }
}

sealed class OltAdministrationUiState {
    data class GetOnuSuccess(val onu: AdministrativeOnuResponse) : OltAdministrationUiState()
    data class Error(val error: String) : OltAdministrationUiState()
    object Empty : OltAdministrationUiState()
    object Loading : OltAdministrationUiState()
    object DeleteOnuSuccess : OltAdministrationUiState()

}
