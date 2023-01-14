package com.dscorp.ispadmin.presentation.technicianslist

import com.example.cleanarchitecture.domain.domain.entity.Technician

/**
 * Created by Sergio Carrillo Diestra on 19/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class TechniciansListResponse {
    class OnTechniciansListFound(val techniciansList: List<Technician>) : TechniciansListResponse()
    class OnError(val error: Exception) : TechniciansListResponse()

}
