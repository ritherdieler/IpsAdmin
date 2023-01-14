package com.dscorp.ispadmin.presentation.plan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.domain.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.Plan
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class PlanViewModel  : ViewModel() {
    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)

    val planResponseLiveData=MutableLiveData<PlanResponse>()
    val formErrorLiveData=MutableLiveData<PlanFormError>()
    fun registerPlan(
        namePlan: String,
        precio: String,
        downloadSpeed: String,
        uploadSpeed: String,
    ){
        if (namePlan.isEmpty()) {
          formErrorLiveData.postValue(PlanFormError.OnEtNamePlanError("El nombre del plan no puede estar vacio"))
            println("Debes escribir el nombre del plan")
            return
        }

        if (precio.isEmpty()) {
            formErrorLiveData.postValue(PlanFormError.OnEtPriceError("El precio no puede estar vacio"))
            println("Debes escribir el precio")
            return
        }

        if (downloadSpeed.isEmpty()) {
             formErrorLiveData.postValue(PlanFormError.OnEtDowloadSpeedError("La velocidad de descarga no puede estar vacia"))
            println("debes escribir la velocidad de descarga")
            return
        }

        if (uploadSpeed.isEmpty()) {
            formErrorLiveData.postValue(PlanFormError.OnEtUploadSpeedError("La velocidad de subida no puede estar vacia"))
            print("Debes escribir la velocidad de subida")
            return
        }

        var planObject = Plan(
            name = namePlan ,
            price =precio.toDouble(),
            downloadSpeed =downloadSpeed.toInt(),
            uploadSpeed =uploadSpeed.toInt()
        )

        viewModelScope.launch {
            try {
                var planFromRepository=repository.registerPlan(planObject)
                planResponseLiveData.postValue(PlanResponse.OnPlanRegistered(planFromRepository))
            } catch (error: Exception) {
                planResponseLiveData.postValue(PlanResponse.OnError(error))

            }

        }

    }
}