package com.dscorp.ispadmin.presentation.registration

import com.dscorp.ispadmin.domain.entity.User

/**
 * Created by Sergio Carrillo Diestra on 16/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class RegisterResponse {
    class OnRegister(val register: User) : RegisterResponse()
    class OnError(val error: Exception) : RegisterResponse()
}
