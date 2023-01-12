package com.dscorp.ispadmin.presentation.login

import com.dscorp.ispadmin.presentation.plan.PlanFormError

/**
 * Created by Sergio Carrillo Diestra on 14/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class LoginFormError {
    class OnEtUser(val error: String) : LoginFormError()
    class OnEtPassword(val error: String) : LoginFormError()
}

