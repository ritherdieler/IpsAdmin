package com.dscorp.ispadmin.presentation.plan

import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.repository.Repository
import com.dscorp.ispadmin.repository.model.Plan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    val planLiveData: MutableLiveData<Plan> = MutableLiveData()
    val errorLiveData: MutableLiveData<Exception> = MutableLiveData()
    // TODO: Implement the ViewModel

    fun validateForm(
        namePlan: String,
        precio: String,
        downloadSpeed: String,
        uploadSpeed: String,
        etNamePlan: EditText,
        etPrecio: EditText,
        etDownloadSpeed: EditText,
        etUploadSpeed: EditText
    ){

        if (namePlan.isEmpty()) {
            println("Debes escribir el nombre del plan")
            etNamePlan.setError("El nombre del plan no puede estar vacio")
            return
        }

        if (precio.isEmpty()) {
            println("Debes escribir el precio")
            etPrecio.setError("El precio no puede estar vacio")
        }
        if (downloadSpeed.isEmpty()) {
            println("debes escribir la velocidad de descarga")
            etDownloadSpeed.setError("La velocidad de descarga no puede estar vacia")
        }

        if (uploadSpeed.isEmpty()) {
            print("Debes escribir la velocidad de subida")
            etUploadSpeed.setError("La velocidad de subida no puede estar vacia")
        }

        var planObject = Plan(
            name = namePlan ,
            price =precio.toDouble(),
            downloadSpeed =downloadSpeed.toInt(),
            uploadSpeed =uploadSpeed.toInt()
        )


        viewModelScope.launch {
            try {

                var response = repository.registerPlan(planObject)
                planLiveData.postValue(response)

            } catch (error: Exception) {
                errorLiveData.postValue(error)

            }

        }

    }
}