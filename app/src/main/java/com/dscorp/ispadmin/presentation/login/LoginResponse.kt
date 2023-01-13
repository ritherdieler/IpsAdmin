package com.dscorp.ispadmin.presentation.login

import com.dscorp.ispadmin.domain.entity.User

/**
 * Created by Sergio Carrillo Diestra on 14/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class LoginResponse {
    class OnLoginSucess(val login: User) : LoginResponse()
    class OnError(val error: Exception) : LoginResponse()
}
