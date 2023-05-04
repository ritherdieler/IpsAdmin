package com.dscorp.ispadmin.presentation.ui.features.mufas

import androidx.lifecycle.MutableLiveData
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.example.data2.data.repository.IRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MufaViewModel(val repository: IRepository) : BaseViewModel<MufaUiState>(), KoinComponent {


    init {
        getMufas()
    }

    private fun getMufas() = executeWithProgress {
        val mufas = repository.getMufas()
        uiState.value = BaseUiState( MufaUiState.OnMufasListFound(mufas))
    }
}