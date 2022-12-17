package com.dscorp.ispadmin.presentation.registration

/**
 * Created by Sergio Carrillo Diestra on 16/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class RegisterFormError{
    class OnEtUserError(val error: String) : RegisterFormError()
    class OnEtFirstNameError(val error: String) : RegisterFormError()
    class OnEtLastNameError(val error: String) : RegisterFormError()
    class OnEtPassword1Error(val error: String) : RegisterFormError()
    class OnEtPassword2Error(val error: String) : RegisterFormError()

}
