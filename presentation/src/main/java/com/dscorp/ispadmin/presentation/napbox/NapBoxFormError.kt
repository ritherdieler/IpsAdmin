package com.dscorp.ispadmin.presentation.napbox

import com.dscorp.ispadmin.presentation.subscription.SubscriptionFormError

/**
 * Created by Sergio Carrillo Diestra on 20/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class NapBoxFormError {
    class OnEtNameNapBoxError(val error: String) : NapBoxFormError()
    class OnEtAbbreviationError(val error: String) : NapBoxFormError()
    class OnEtLocationError(val error: String) : NapBoxFormError()

}
