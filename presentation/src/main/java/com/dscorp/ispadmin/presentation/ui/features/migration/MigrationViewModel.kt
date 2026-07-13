package com.dscorp.ispadmin.presentation.ui.features.migration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.data.apirequestmodel.MigrationRequest
import com.dscorp.ispadmin.data.repository.IRepository
import com.dscorp.ispadmin.domain.model.InstallationType
import com.dscorp.ispadmin.domain.model.Onu
import com.dscorp.ispadmin.domain.model.PlanResponse
import com.dscorp.ispadmin.domain.model.SubscriptionResponse
import com.dscorp.ispadmin.observability.ObsBreadcrumbCategory
import com.dscorp.ispadmin.observability.ObservabilityClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MigrationViewModel(
    private val repository: IRepository,
    private val observabilityClient: ObservabilityClient
) : ViewModel() {

    private companion object {
        const val OBS_FEATURE = "migration"
        const val OBS_SCREEN = "migration"
    }

    private val _uiState = MutableStateFlow<MigrationUiState>(MigrationUiState.Empty)
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.Lazily, MigrationUiState.Empty)
    private var refreshOnusJob: Job? = null

    fun doMigration(migrationRequest: MigrationRequest) = viewModelScope.launch {
        observabilityClient.addBreadcrumb(
            category = ObsBreadcrumbCategory.USER_ACTION,
            message = "$OBS_FEATURE.migrate",
            data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN)
        )
        if (migrationRequest.isValid().not()) {
            _uiState.value = MigrationUiState.Error(Exception("Datos incorrectos"))
            return@launch
        } else {
            try {
                _uiState.emit(MigrationUiState.Loading)
                val response =
                    repository.doMigration(migrationRequest)
                _uiState.emit(MigrationUiState.Success(response))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                observabilityClient.reportError(
                    throwable = e,
                    message = "Fallo al ejecutar migración",
                    tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "migrate")
                )
                _uiState.emit(MigrationUiState.Error(e))
            }
        }

    }

    fun getMigrationFormData(subscriptionId: Int) = viewModelScope.launch {
        observabilityClient.addBreadcrumb(
            category = ObsBreadcrumbCategory.NAVIGATION,
            message = "$OBS_FEATURE.load_form_data",
            data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "entityId" to subscriptionId)
        )
        try {
            val subscription = async { repository.subscriptionById(subscriptionId) }.await()
            _uiState.emit(MigrationUiState.Loading)
            val unconfirmedOnus = async { repository.getUnconfirmedOnus() }.await()
            val plans = async { repository.getPlans() }.await()
                .filter { it.type == InstallationType.FIBER }
            _uiState.emit(MigrationUiState.FormDataReady(plans, unconfirmedOnus, subscription))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            observabilityClient.reportError(
                throwable = e,
                message = "Fallo al cargar datos de formulario de migración",
                tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "load_form_data", "entityId" to subscriptionId)
            )
            _uiState.emit(MigrationUiState.Error(e))
        }
    }

    fun refreshOnusDebounced(subscriptionId: Int) {
        val currentState = _uiState.value
        if (currentState is MigrationUiState.FormDataReady && !currentState.isRefreshingOnuList) {
            _uiState.value = currentState.copy(isRefreshingOnuList = true)
        }
        refreshOnusJob?.cancel()
        refreshOnusJob = viewModelScope.launch {
            delay(1000)
            refreshOnus(subscriptionId)
        }
    }

    private suspend fun refreshOnus(subscriptionId: Int) {
        try {
            val currentState = _uiState.value
            if (currentState is MigrationUiState.FormDataReady) {
                val unconfirmedOnus = repository.getUnconfirmedOnus()
                _uiState.emit(
                    currentState.copy(
                        unconfirmedOnus = unconfirmedOnus,
                        isRefreshingOnuList = false
                    )
                )
                return
            }
            val unconfirmedOnus = repository.getUnconfirmedOnus()
            val subscription = repository.subscriptionById(subscriptionId)
            val plans = repository.getPlans().filter { it.type == InstallationType.FIBER }
            _uiState.emit(
                MigrationUiState.FormDataReady(
                    plans = plans,
                    unconfirmedOnus = unconfirmedOnus,
                    subscription = subscription,
                    isRefreshingOnuList = false
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            observabilityClient.reportError(
                throwable = e,
                message = "Fallo al refrescar ONUs en migración",
                tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "refresh_onus", "entityId" to subscriptionId)
            )
            _uiState.emit(MigrationUiState.Error(e))
        }
    }

}

sealed class MigrationUiState {

    object Empty : MigrationUiState()
    object Loading : MigrationUiState()
    data class Success(val subscriptionResponse: SubscriptionResponse) : MigrationUiState()
    data class Error(val error: Exception) : MigrationUiState()
    data class FormDataReady(
        val plans: List<PlanResponse>,
        val unconfirmedOnus: List<Onu>,
        val subscription: SubscriptionResponse,
        val isRefreshingOnuList: Boolean = false
    ) :
        MigrationUiState()
}
