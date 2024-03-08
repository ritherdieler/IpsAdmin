package com.dscorp.ispadmin.presentation.ui.features.dashboard

import androidx.lifecycle.MutableLiveData
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.example.data2.data.extensions.encryptWithSHA384
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DashBoardViewModel : BaseViewModel<DashBoardDataUiState>(), KoinComponent {
    private val repository: IRepository by inject()
    val userSession = repository.getUserSession()
    val showDashBoardShimmerLiveData = MutableLiveData(true)

    init {
        getDashBoardData()
    }

    fun getDashBoardData() = executeNoProgress (doFinally = {
        showDashBoardShimmerLiveData.value = false
    }) {
        val response = repository.getDashBoardData()
        uiState.value = BaseUiState(DashBoardDataUiState.DashBoardData(response))
    }

    fun startServiceCut(password: String) = executeWithProgress() {
        val pass = password.encryptWithSHA384()
        val storedpass = userSession?.password?:""

        if (pass != storedpass) {
            uiState.value =
                BaseUiState(DashBoardDataUiState.InvalidPasswordError("Contraseña incorrecta"))
            return@executeWithProgress
        }
        repository.startServiceCut()
        uiState.value = BaseUiState(DashBoardDataUiState.CutServiceSuccess)
    }
}