package com.dscorp.ispadmin.presentation.ui.features.napbox.register

import com.example.cleanarchitecture.domain.domain.entity.NapBox

/**
 * Created by Sergio Carrillo Diestra on 20/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class NapBoxSealedClassResponse {
    class OnNapBoxSealedClassRegister(val napBox: NapBox) : NapBoxSealedClassResponse()
    class OnError(val error: Exception) : NapBoxSealedClassResponse()
}
