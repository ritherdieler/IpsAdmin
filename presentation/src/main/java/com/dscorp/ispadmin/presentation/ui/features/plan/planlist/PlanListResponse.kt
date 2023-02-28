package com.dscorp.ispadmin.presentation.ui.features.plan.planlist

import com.example.cleanarchitecture.domain.domain.entity.NapBoxResponse
import com.example.cleanarchitecture.domain.domain.entity.PlanResponse

/**
 * Created by Sergio Carrillo Diestra on 19/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class PlanListResponse {
    class OnPlanListFound(val planList: List<PlanResponse>) : PlanListResponse()
    class OnError(val error: Exception) : PlanListResponse()
}
