package com.dscorp.ispadmin.presentation.ui.features.main

import com.example.cleanarchitecture.domain.domain.entity.User

sealed class UiState {
    object Idle : UiState()
    class UserSessionsFound(val response: User) : UiState()
}
