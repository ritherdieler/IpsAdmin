package com.dscorp.ispadmin.presentation.napbox

import com.example.cleanarchitecture.domain.domain.entity.NapBox

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
