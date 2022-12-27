package com.dscorp.ispadmin.presentation.technician

import com.dscorp.ispadmin.presentation.subscription.SubscriptionResponse
import com.dscorp.ispadmin.repository.model.*

/**
 * Created by Sergio Carrillo Diestra on 26/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class TechnicianResponse{

    class OnTechnicianRegistered(val technician: Technician) : TechnicianResponse()
    class OnError(val error: Exception) : TechnicianResponse()
}
