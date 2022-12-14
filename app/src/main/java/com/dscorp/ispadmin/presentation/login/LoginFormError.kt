package com.dscorp.ispadmin.presentation.login

/**
 * Created by Sergio Carrillo Diestra on 14/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class LoginFormError{
    class OnEtUserError(val error:String):LoginFormError()
    class OnEtPasswordError(val error: String):LoginFormError()
}
