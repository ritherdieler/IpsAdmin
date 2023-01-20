package com.dscorp.ispadmin.presentation.serviceorden

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.ServiceOrder
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class ServiceOrderViewModel  : ViewModel() {
    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)

    val serviceOrderResponseLiveData=MutableLiveData<ServiceOrderResponse>()
    val serviceOrderFormErrorLiveData=MutableLiveData<ServiceOrderFormError>()

    fun registerServiceOrder(serviceOrder: ServiceOrder)=viewModelScope.launch {

        try {
            if (formatIsValid(serviceOrder)) {
                var serviceOrderFromRepository = repository.registerServiceOrder(serviceOrder)
                serviceOrderResponseLiveData.postValue(
                    ServiceOrderResponse.OnServiceOrderRegistered
                        (serviceOrderFromRepository)
                )
            }
        } catch (error: Exception) {
            serviceOrderResponseLiveData.postValue(ServiceOrderResponse.OnError(error))
        }
    }

    private fun formatIsValid(serviceOrder: ServiceOrder): Boolean {

        if (serviceOrder.latitude.toString().isEmpty()) {
          serviceOrderFormErrorLiveData.postValue(ServiceOrderFormError.OnEtLatitudeError("La Latitud del  " +
                  "servicio no puede estar vacio"))
            return false
        }

        if (serviceOrder.longitude.toString().isEmpty()) {
            serviceOrderFormErrorLiveData.postValue(ServiceOrderFormError.OnEtLogintudeError("La Longitud no puede estar " +
                    "vacio"))
            return false
        }

        if (serviceOrder.createDate == 0L) {
             serviceOrderFormErrorLiveData.postValue(ServiceOrderFormError.OnEtCreateDateError("La Fecha de creacion  " +
                     " no puede estar vacia"))
            return false
        }
        if (serviceOrder.attentionDate == 0L) {
            serviceOrderFormErrorLiveData.postValue(ServiceOrderFormError.OnEtAttentionDate("La Fecha de atencion no " +
                    "puede estar vacia"))
            return false
        }
        return true
    }
}