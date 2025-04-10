package com.dscorp.ispadmin.presentation.ui.features.registration

import com.example.cleanarchitecture.domain.entity.User

/**
 * Created by Sergio Carrillo Diestra on 16/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class RegisterUiState {
    class OnRegister(val register: User) : RegisterUiState()
}
