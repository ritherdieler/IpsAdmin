package com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose

import com.example.cleanarchitecture.domain.entity.User
import com.example.data2.data.repository.IRepository

class GetUserSessionUseCase(private val repository: IRepository) {
    suspend operator fun invoke(): Result<User?> = runCatching {
        repository.getUserSession()
    }
}
