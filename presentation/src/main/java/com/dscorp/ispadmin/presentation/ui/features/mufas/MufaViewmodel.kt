package com.dscorp.ispadmin.presentation.ui.features.mufas

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MufaViewmodel: ViewModel(),KoinComponent {

    val repository: IRepository by inject()
    val mufaUiStateLiveData = MutableLiveData<MufaUiState>()

    init {
        initGetMufas()
    }

    private fun initGetMufas() = viewModelScope.launch {
        try {
            val mufaListFromRepository = repository.getMufas()
            mufaUiStateLiveData.postValue(
                MufaUiState.OnMufasListFound(
                    mufaListFromRepository
                )
            )
        } catch (error: Exception) {
            error.printStackTrace()
            mufaUiStateLiveData.postValue(MufaUiState.OnError(error))
        }
    }
}