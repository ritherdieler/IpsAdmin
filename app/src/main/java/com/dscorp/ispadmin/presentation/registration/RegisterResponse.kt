package com.dscorp.ispadmin.presentation.registration

import com.dscorp.ispadmin.repository.model.User
import java.lang.Error

/**
 * Created by Sergio Carrillo Diestra on 16/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class RegisterResponse{
    class OnRegister(val register:User):RegisterResponse()
    class OnError(val error: Exception):RegisterResponse()
}
