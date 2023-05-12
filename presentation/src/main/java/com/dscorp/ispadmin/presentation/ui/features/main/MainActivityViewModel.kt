package com.dscorp.ispadmin.presentation.ui.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel(repository: IRepository) : ViewModel() {

    val user = repository.getUserSession()

}
