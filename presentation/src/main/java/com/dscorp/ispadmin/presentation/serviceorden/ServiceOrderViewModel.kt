package com.dscorp.ispadmin.presentation.serviceorden

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.domain.repository.IRepository
import com.example.cleanarchitecture.domain.domain.entity.ServiceOrder
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class ServiceOrderViewModel  : ViewModel() {
    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)

    val serviceOrderResponseLiveData=MutableLiveData<ServiceOrderResponse>()
    val serviceOrderFormErrorLiveData=MutableLiveData<ServiceOrderFormError>()
    fun registerServiceOrder(
        longitude: Double,
        latitude: Double,
        createDate: Long,
        attentionDate:Long,

    ){
        if (latitude.toString().isEmpty()) {
          serviceOrderFormErrorLiveData.postValue(ServiceOrderFormError.OnEtLatitudeError("La Latitud del  " +
                  "servicio no puede estar vacio"))
            return
        }

        if (longitude.toString().isEmpty()) {
            serviceOrderFormErrorLiveData.postValue(ServiceOrderFormError.OnEtLogintudeError("La Longitud no puede estar " +
                    "vacio"))
            return
        }

        if (createDate == 0L) {
             serviceOrderFormErrorLiveData.postValue(ServiceOrderFormError.OnEtCreateDateError("La Fecha de creacion  " +
                     " no puede estar vacia"))
            return
        }
        if (attentionDate == 0L) {
            serviceOrderFormErrorLiveData.postValue(ServiceOrderFormError.OnEtAttentionDate("La Fecha de atencion no " +
                    "puede estar vacia"))
            return
        }

        var serviceOrderObject = ServiceOrder(
            longitude = longitude,
            latitude = latitude,
            createDate = createDate,
            attentionDate = attentionDate,
        )

        viewModelScope.launch {
            try {
                var serviceOrderFromRepository=repository.registerServiceOrder(serviceOrderObject)
                serviceOrderResponseLiveData.postValue(ServiceOrderResponse.OnServiceOrderRegistered
                    (serviceOrderFromRepository))
            } catch (error: Exception) {
                serviceOrderResponseLiveData.postValue(ServiceOrderResponse.OnError(error))

            }
        }

    }
}