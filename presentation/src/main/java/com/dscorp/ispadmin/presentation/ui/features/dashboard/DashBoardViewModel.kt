package com.dscorp.ispadmin.presentation.ui.features.dashboard

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
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
        delay(500)
        uiState.value = BaseUiState(DashBoardDataUiState.DashBoardData(response))
    }

    fun startServiceCut(password: String) = executeWithProgress {
        if (password != (userSession?.password ?: "")) {
            uiState.value =
                BaseUiState(DashBoardDataUiState.InvalidPasswordError("Contraseña incorrecta"))
            return@executeWithProgress
        }
        repository.startServiceCut()
        uiState.value = BaseUiState(DashBoardDataUiState.CutServiceSuccess)
    }
}