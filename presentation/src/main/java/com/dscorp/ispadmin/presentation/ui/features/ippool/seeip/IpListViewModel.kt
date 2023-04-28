package com.dscorp.ispadmin.presentation.ui.features.ippool.seeip

import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class IpListViewModel(private val repository: IRepository) : BaseViewModel<IpListUiState>(),
    KoinComponent {

    fun getIpList(poolId: Int) = executeWithProgress {
        val response = repository.getIpList(poolId)
        uiState.value = BaseUiState(IpListUiState.IpListReady(response))
    }

}