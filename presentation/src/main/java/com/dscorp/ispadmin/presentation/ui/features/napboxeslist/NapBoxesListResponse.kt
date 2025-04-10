package com.dscorp.ispadmin.presentation.ui.features.napboxeslist


import com.example.cleanarchitecture.domain.entity.NapBoxResponse

/**
 * Created by Sergio Carrillo Diestra on 19/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class NapBoxesListResponse {
    class OnNapBoxesListFound(val napBoxesList: List<NapBoxResponse>) : NapBoxesListResponse()
    class OnError(val error: Exception) : NapBoxesListResponse()
}
