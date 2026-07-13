package com.dscorp.ispadmin.presentation.ui.features.oltadministrator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.data.repository.IRepository
import com.dscorp.ispadmin.data.response.AdministrativeOnuResponse
import com.dscorp.ispadmin.observability.ObsBreadcrumbCategory
import com.dscorp.ispadmin.observability.ObservabilityClient
import com.dscorp.ispadmin.presentation.ui.features.oltadministrator.OltAdministrationUiState.DeleteOnuSuccess
import com.dscorp.ispadmin.presentation.ui.features.oltadministrator.OltAdministrationUiState.Empty
import com.dscorp.ispadmin.presentation.ui.features.oltadministrator.OltAdministrationUiState.Error
import com.dscorp.ispadmin.presentation.ui.features.oltadministrator.OltAdministrationUiState.GetOnuSuccess
import com.dscorp.ispadmin.presentation.ui.features.oltadministrator.OltAdministrationUiState.Loading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OltAdministrationViewModel(
    private val repository: IRepository,
    private val observabilityClient: ObservabilityClient
) : ViewModel() {

    private companion object {
        const val OBS_FEATURE = "olt"
        const val OBS_SCREEN = "olt_administration"
    }

    private val _uiState = MutableStateFlow<OltAdministrationUiState>(Empty)
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.Lazily, Empty)
    fun getOnuBySn(onuSn: String) = viewModelScope.launch {
        observabilityClient.addBreadcrumb(
            category = ObsBreadcrumbCategory.USER_ACTION,
            message = "$OBS_FEATURE.get_onu",
            data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "onuSn" to onuSn)
        )
        try {
            _uiState.value = Loading
            val response = repository.getOnuBySn(onuSn)
            _uiState.value = GetOnuSuccess(response)
        } catch (e: Exception) {
            observabilityClient.reportError(
                throwable = e,
                message = "Fallo al obtener ONU por número de serie",
                tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "get_onu")
            )
            _uiState.value = Error(e.message ?: "Error")
        }
    }

    fun showForm() {
        _uiState.value = Empty
    }

    fun deleteOnuFromOlt(onuExternalId:String) =viewModelScope.launch{
        observabilityClient.addBreadcrumb(
            category = ObsBreadcrumbCategory.USER_ACTION,
            message = "$OBS_FEATURE.delete_onu",
            data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "entityId" to onuExternalId)
        )
        try {
            _uiState.value = Loading
            repository.deleteOnuFromOlt(onuExternalId)
            _uiState.value = DeleteOnuSuccess
        } catch (e: Exception) {
            observabilityClient.reportError(
                throwable = e,
                message = "Fallo al eliminar ONU de la OLT",
                tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "delete_onu", "entityId" to onuExternalId)
            )
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
