package com.dscorp.ispadmin.presentation.ui.features.networkdevice.networkdevicelist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NetworkDeviceListViewModel : ViewModel(), KoinComponent {

    val repository: IRepository by inject()
    val responseLiveData = MutableLiveData<NetworkDeviceListResponse>()

    init {
        initGetSubscriptions()
    }

    private fun initGetSubscriptions() = viewModelScope.launch {
        try {
            val networkDeviceListFromRepository = repository.getDevices()
            responseLiveData.postValue(
                NetworkDeviceListResponse.OnNetworkDeviceListFound(
                    networkDeviceListFromRepository
                )
            )
        } catch (error: Exception) {
            error.printStackTrace()
            responseLiveData.postValue(NetworkDeviceListResponse.OnError(error))
        }
    }
}
