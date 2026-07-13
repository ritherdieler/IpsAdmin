package com.dscorp.ispadmin.presentation.ui.features.fixedCost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.data.apirequestmodel.FixedCostRequest
import com.dscorp.ispadmin.data.repository.IRepository
import com.dscorp.ispadmin.domain.model.FixedCost
import com.dscorp.ispadmin.observability.ObsBreadcrumbCategory
import com.dscorp.ispadmin.observability.ObservabilityClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

sealed class SaveFixedCostState(val message: String? = null) {
    object Loading : SaveFixedCostState()
    object Success : SaveFixedCostState("Gasto fijo registrado correctamente")
    object Error : SaveFixedCostState("Error al registrar gasto fijo")
}

sealed class GetAllFixedCostsState(val message: String? = null) {
    object Loading : GetAllFixedCostsState()
    data class Success(val fixedCosts: List<FixedCost>) : GetAllFixedCostsState()
    object Error : GetAllFixedCostsState("Error al obtener los gastos fijos")
}
class FixedCostViewModel(
    private val repository: IRepository,
    private val observabilityClient: ObservabilityClient
) : ViewModel() {

    private companion object {
        const val OBS_FEATURE = "fixed_cost"
        const val OBS_SCREEN = "fixed_cost"
    }

    val user = repository.getUserSession()

    val saveFixedCostFlow = MutableStateFlow<SaveFixedCostState>(SaveFixedCostState.Loading)

    val getAllFixedCostsFlow = MutableStateFlow<GetAllFixedCostsState>(GetAllFixedCostsState.Loading)

    fun saveFixedCost(fixedCostRequest: FixedCostRequest) = viewModelScope.launch {
        observabilityClient.addBreadcrumb(
            category = ObsBreadcrumbCategory.USER_ACTION,
            message = "$OBS_FEATURE.save",
            data = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN)
        )
        try {
            fixedCostRequest.userId = user?.id!!
            if (!fixedCostRequest.isValid()) return@launch
            saveFixedCostFlow.value = SaveFixedCostState.Loading
            repository.saveFixedCost(fixedCostRequest)
            saveFixedCostFlow.value = SaveFixedCostState.Success
        } catch (e: Exception) {
            observabilityClient.reportError(
                throwable = e,
                message = "Fallo al registrar gasto fijo",
                tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "save")
            )
            e.printStackTrace()
            saveFixedCostFlow.value = SaveFixedCostState.Error
        }
    }


    fun getAllFixedCosts() = viewModelScope.launch {
        try {
            getAllFixedCostsFlow.value = GetAllFixedCostsState.Loading
            val fixedCosts = repository.getAllFixedCosts()
            getAllFixedCostsFlow.value = GetAllFixedCostsState.Success(fixedCosts)
        } catch (e: Exception) {
            observabilityClient.reportError(
                throwable = e,
                message = "Fallo al obtener gastos fijos",
                tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "load_all")
            )
            e.printStackTrace()
            getAllFixedCostsFlow.value = GetAllFixedCostsState.Error
        }
    }


}