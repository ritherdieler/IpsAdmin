package com.dscorp.ispadmin.presentation.plan

import com.dscorp.ispadmin.repository.model.Plan

/**
 * Created by Sergio Carrillo Diestra on 14/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class PlanResponse{
    class OnPlanRegistered(val plan :Plan):PlanResponse()
    class OnError(val error:Exception):PlanResponse()
}
