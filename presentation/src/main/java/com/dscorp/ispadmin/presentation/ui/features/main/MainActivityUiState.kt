package com.dscorp.ispadmin.presentation.ui.features.main

import com.example.cleanarchitecture.domain.domain.entity.User

sealed class MainActivityUiState {
    object Idle : MainActivityUiState()
    class UserSessionsFound(val response: User) : MainActivityUiState()
}
