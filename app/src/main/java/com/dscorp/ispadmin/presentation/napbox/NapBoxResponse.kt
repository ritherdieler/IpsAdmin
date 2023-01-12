package com.dscorp.ispadmin.presentation.napbox

import com.dscorp.ispadmin.repository.model.NapBox

/**
 * Created by Sergio Carrillo Diestra on 20/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class NapBoxResponse {
    class OnNapBoxRegister(val napBox: NapBox) : NapBoxResponse()
    class OnError(val error: Exception) : NapBoxResponse()
}
