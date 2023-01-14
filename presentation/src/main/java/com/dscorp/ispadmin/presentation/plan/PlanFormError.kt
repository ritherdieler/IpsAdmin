package com.dscorp.ispadmin.presentation.plan

/**
 * Created by Sergio Carrillo Diestra on 14/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class PlanFormError {
    class OnEtNamePlanError(val error: String) : PlanFormError()
    class OnEtPriceError(val error: String) : PlanFormError()
    class OnEtDowloadSpeedError(val error: String) : PlanFormError()
    class OnEtUploadSpeedError(val error: String) : PlanFormError()
}
