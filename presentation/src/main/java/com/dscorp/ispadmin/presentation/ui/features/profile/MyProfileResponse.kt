package com.dscorp.ispadmin.presentation.ui.features.profile

import com.example.cleanarchitecture.domain.entity.User

sealed class MyProfileResponse {
    class UserSessionFound(val user: User) : MyProfileResponse()
    class OnError(val error: Exception) : MyProfileResponse()
}
