package com.dscorp.ispadmin.presentation.ui.features.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent

class MyProfileViewModel : ViewModel(), KoinComponent {
    private val repository: IRepository by KoinJavaComponent.inject(IRepository::class.java)
    val myProfileLiveData = MutableLiveData<MyProfileResponse>()
    init {
        viewModelScope.launch {
            val response = repository.getUserSession()
            response?.let {
                myProfileLiveData.postValue(MyProfileResponse.UserSessionFound(it))
            }
        }
    }
    fun logOut() {
        viewModelScope.launch {
            repository.clearUserSession()
        }
    }
}
