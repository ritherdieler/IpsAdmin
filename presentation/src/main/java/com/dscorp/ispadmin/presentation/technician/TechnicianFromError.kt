package com.dscorp.ispadmin.presentation.technician

import com.dscorp.ispadmin.presentation.subscription.SubscriptionFormError

/**
 * Created by Sergio Carrillo Diestra on 26/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class TechnicianFromError {

    class OnEtFirstNameError(val error: String) : TechnicianFromError()
    class OnEtLastNameError(val error: String) : TechnicianFromError()
    class OnEtDniError(val error: String) : TechnicianFromError()
    class OnEtTypeError(val error: String) : TechnicianFromError()
    class OnEtUserNameError(val error: String) : TechnicianFromError()
    class OnEtPasswordError(val error: String) : TechnicianFromError()
    class OnEtAddressError(val error: String) : TechnicianFromError()
    class OnEtPhoneError(val error: String) : TechnicianFromError()
    class OnEtBirthdayError(val error: String) : TechnicianFromError()
}

