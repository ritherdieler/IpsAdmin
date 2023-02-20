package com.dscorp.ispadmin.presentation.ui.features.dashboard

import com.example.cleanarchitecture.domain.domain.entity.DashBoardDataResponse

sealed class DashBoardDataUiState {
    class DashBoardData(val response: DashBoardDataResponse) : DashBoardDataUiState()
    class DashBoardDataError(val message: String) : DashBoardDataUiState()

}
