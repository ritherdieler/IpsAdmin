package com.dscorp.ispadmin.presentation.ui.features.subscription.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.data.apirequestmodel.UpdateSubscriptionPlanBody
import com.dscorp.ispadmin.data.repository.IRepository
import com.dscorp.ispadmin.domain.model.InstallationType
import com.dscorp.ispadmin.domain.model.PlanResponse
import com.dscorp.ispadmin.domain.model.SubscriptionResponse
import com.dscorp.ispadmin.observability.ObsBreadcrumbCategory
import com.dscorp.ispadmin.observability.ObservabilityClient
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI State según patrón UDF
data class EditSubscriptionUIState(
    val isLoading: Boolean = false,
    val subscriptionData: SubscriptionResponse? = null,
    val plans: List<PlanResponse> = emptyList(),
    val error: String? = null,
    val isSuccess: Boolean = false,
    val form: PlanEditForm = PlanEditForm()
)

// Data class para el formulario de edición de plan
data class PlanEditForm(
    val selectedPlan: PlanResponse? = null,
    val touched: Boolean = false
) {
    // Validación del formulario
    val isValid: Boolean get() = selectedPlan != null

}

class EditSubscriptionViewModel(
    private val repository: IRepository,
    private val observabilityClient: ObservabilityClient,
) : ViewModel() {

    private companion object {
        const val OBS_FEATURE = "subscription"
        const val OBS_SCREEN = "edit_subscription"
    }

    // Estado interno mutable
    private val _uiState = MutableStateFlow(EditSubscriptionUIState())
    
    // Estado público inmutable
    val uiState: StateFlow<EditSubscriptionUIState> = _uiState.asStateFlow()

    val user = repository.getUserSession()

    fun getFormData(subscriptionId: Int) {
        viewModelScope.launch {
            observabilityClient.addBreadcrumb(
                category = ObsBreadcrumbCategory.NAVIGATION,
                message = "$OBS_FEATURE.load_form_data",
                data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "entityId" to subscriptionId)
            )
            try {
                // Actualizamos estado a cargando
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                // Cargamos datos en paralelo
                val subscriptionDeferred = async { repository.subscriptionById(subscriptionId) }
                val plansDeferred = async { repository.getPlans() }

                val subscriptionResponse = subscriptionDeferred.await()
                val plans = plansDeferred.await()

                val allowedPlanTypes = when (subscriptionResponse.installationType) {
                    InstallationType.FIBER, InstallationType.ONLY_TV_FIBER ->
                        setOf(InstallationType.FIBER, InstallationType.ONLY_TV_FIBER)
                    InstallationType.WIRELESS ->
                        setOf(InstallationType.WIRELESS)
                }

                val filteredPlans = plans.filter { plan -> plan.type in allowedPlanTypes }

                // Encontramos el plan actual del suscriptor
                val selectedPlan = filteredPlans.find { it.id == subscriptionResponse.plan?.id }
                
                // Actualizamos el estado con los datos cargados y planes filtrados
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        subscriptionData = subscriptionResponse,
                        plans = filteredPlans,
                        form = PlanEditForm(selectedPlan = selectedPlan)
                    ) 
                }
            } catch (e: Exception) {
                observabilityClient.reportError(
                    throwable = e,
                    message = "Fallo al cargar datos de edición de suscripción",
                    tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "load_form_data", "entityId" to subscriptionId)
                )
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error desconocido al cargar datos"
                    ) 
                }
            }
        }
    }

    fun updateSelectedPlan(plan: PlanResponse?) {
        _uiState.update { currentState ->
            currentState.copy(
                form = currentState.form.copy(
                    selectedPlan = plan,
                    touched = true
                )
            )
        }
    }

    fun editSubscription(subscriptionId: Int) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                
                if (!currentState.form.isValid) {
                    _uiState.update { it.copy(
                        form = it.form.copy(touched = true),
                        error = "Por favor seleccione un plan"
                    )}
                    return@launch
                }
                
                observabilityClient.addBreadcrumb(
                    category = ObsBreadcrumbCategory.USER_ACTION,
                    message = "$OBS_FEATURE.save_edit",
                    data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "entityId" to subscriptionId)
                )
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val subscription = UpdateSubscriptionPlanBody(
                    subscriptionId = subscriptionId,
                    planId = currentState.form.selectedPlan?.id!!
                )
                
                repository.updateSubscriptionPlan(subscription)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isSuccess = true
                    ) 
                }
                observabilityClient.addBreadcrumb(
                    category = ObsBreadcrumbCategory.STATE,
                    message = "$OBS_FEATURE.save_edit.success",
                    data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "entityId" to subscriptionId)
                )
            } catch (e: Exception) {
                observabilityClient.reportError(
                    throwable = e,
                    message = "Fallo al actualizar plan de suscripción",
                    tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "save_edit", "entityId" to subscriptionId)
                )
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error desconocido al actualizar suscripción"
                    ) 
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun clearSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
