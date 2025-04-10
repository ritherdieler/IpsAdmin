package com.dscorp.ispadmin.presentation.ui.features.napbox.register

import com.example.cleanarchitecture.domain.entity.Mufa
import com.example.cleanarchitecture.domain.entity.NapBox

/**
 * Created by Sergio Carrillo Diestra on 20/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class RegisterNapBoxUiState {
    class OnRegisterNapBoxSealedClassRegister(val napBox: NapBox) : RegisterNapBoxUiState()
    class OnError(val error: Exception) : RegisterNapBoxUiState()
    class MufasReady(val mufas: List<Mufa>) : RegisterNapBoxUiState()
}
