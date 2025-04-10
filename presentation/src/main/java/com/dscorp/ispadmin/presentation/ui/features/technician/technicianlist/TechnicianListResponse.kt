package com.dscorp.ispadmin.presentation.ui.features.technician.technicianlist

import com.example.cleanarchitecture.domain.entity.Technician

/**
 * Created by Sergio Carrillo Diestra on 19/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class TechnicianListResponse {
    class OnTechnicianListFound(val technician: List<Technician>) : TechnicianListResponse()
    class OnError(val error: Exception) : TechnicianListResponse()
}
