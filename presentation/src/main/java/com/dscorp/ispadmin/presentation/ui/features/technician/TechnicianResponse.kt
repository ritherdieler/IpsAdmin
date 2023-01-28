package com.dscorp.ispadmin.presentation.ui.features.technician

import com.example.cleanarchitecture.domain.domain.entity.Technician

/**
 * Created by Sergio Carrillo Diestra on 26/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class TechnicianResponse {

    class OnTechnicianRegistered(val technician: Technician) : TechnicianResponse()
    class OnError(val error: Exception) : TechnicianResponse()
}
