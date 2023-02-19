package com.dscorp.ispadmin.presentation.ui.features.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent

class MyProfileViewmodel : ViewModel(), KoinComponent {
    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)
    val myProfileLiveData = MutableLiveData<MyProfileResponse>()
    init {
        viewModelScope.launch {
            val response = repository.getUserSession()
            myProfileLiveData.postValue(MyProfileResponse.UserSessionFound(response!!))
        }
    }
    fun logOut() {
        viewModelScope.launch {
            repository.clearUserSession()
        }
    }
}
