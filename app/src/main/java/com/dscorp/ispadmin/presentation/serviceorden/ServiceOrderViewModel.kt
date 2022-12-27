package com.dscorp.ispadmin.presentation.serviceorden

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.repository.IRepository
import com.dscorp.ispadmin.repository.model.ServiceOrder
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class ServiceOrderViewModel  : ViewModel() {
    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)

    val serviceOrderResponseLiveData=MutableLiveData<ServiceOrderResponse>()
    val ServiceOrderFormLiveData=MutableLiveData<ServiceOrderFormError>()
    fun registerServiceOrder(
        longitude: Double,
        latitude: Double,
        createDate: Int,
        attentionDate: Int,
    ){
        if (latitude.toString().isEmpty()) {
          ServiceOrderFormLiveData.postValue(ServiceOrderFormError.OnEtLatitudError("La Latitud del  " +
                  "servicio no puede estar vacio"))
            return
        }

        if (longitude.toString().isEmpty()) {
            ServiceOrderFormLiveData.postValue(ServiceOrderFormError.OnEtLogintudError("La Longitud no puede estar " +
                    "vacio"))
            return
        }

        if (createDate.toString().isEmpty()) {
             ServiceOrderFormLiveData.postValue(ServiceOrderFormError.OnEtCreateDateError("La Fecha de creacion  " +
                     " no puede estar vacia"))
            return
        }

        if (attentionDate.toString().isEmpty()) {
            ServiceOrderFormLiveData.postValue(ServiceOrderFormError.OnEtAttentionDate("La Fecha de atencion no " +
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