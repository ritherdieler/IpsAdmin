package com.dscorp.ispadmin.presentation.ui.features.mufas

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MufaViewModel : BaseViewModel<MufaUiState>(), KoinComponent {

    val repository: IRepository by inject()
    val mufaUiStateLiveData = MutableLiveData<MufaUiState>()

    init {
        initGetMufas()
    }

    private fun initGetMufas() = executeWithProgress {
        val mufaListFromRepository = repository.getMufas()
        uiState.value = BaseUiState( MufaUiState.OnMufasListFound(mufaListFromRepository))
    }
}