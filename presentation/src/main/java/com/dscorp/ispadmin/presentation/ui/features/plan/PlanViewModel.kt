package com.dscorp.ispadmin.presentation.ui.features.plan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.Plan
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class PlanViewModel : ViewModel() {
    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)

    val planResponseLiveData = MutableLiveData<PlanResponse>()
    val formErrorLiveData = MutableLiveData<PlanFormError>()
    fun registerPlan(plan: Plan) = viewModelScope.launch {

        try {
            if (formIsValid(plan)) {
                val planFromRepository = repository.registerPlan(plan)
                planResponseLiveData.postValue(PlanResponse.OnPlanRegistered(planFromRepository))
            }
        } catch (error: Exception) {
            planResponseLiveData.postValue(PlanResponse.OnError(error))
        }
    }

    private fun formIsValid(plan: Plan): Boolean {
        if (plan.name.isEmpty()) {
            formErrorLiveData.postValue(PlanFormError.OnEtNamePlanError("El nombre del plan no puede estar vacio"))
            return false

        }

        if (plan.price == 0.0) {
            formErrorLiveData.postValue(PlanFormError.OnEtPriceError("El precio no puede estar vacio"))
            return false
        }

        if (plan.downloadSpeed.isEmpty()) {
            formErrorLiveData.postValue(PlanFormError.OnEtDowloadSpeedError("La velocidad de descarga no puede estar vacia"))
            return false
        }

        if (plan.uploadSpeed.isEmpty()) {
            formErrorLiveData.postValue(PlanFormError.OnEtUploadSpeedError("La velocidad de subida no puede estar vacia"))
            return false
        }
        return true
    }
}