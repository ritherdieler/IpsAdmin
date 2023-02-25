package com.dscorp.ispadmin.presentation.ui.features.ippool.seeip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class IpListViewModel(private val repository: IRepository) : ViewModel(),KoinComponent {

    val uiState = MutableStateFlow<IpListUiState>(IpListUiState.Idle)


    fun getIpList(poolId: Int) = viewModelScope.launch {
        try {
            val response = repository.getIpList(poolId)
            uiState.emit(IpListUiState.IpListReady(response))
        } catch (e: Exception) {
            uiState.emit(IpListUiState.IpListError(e.message ?: ""))
            e.printStackTrace()
        }

    }


}