package com.dscorp.ispadmin.presentation.ui.features.plan

/**
 * Created by Sergio Carrillo Diestra on 14/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class PlanErrorCleanForm {
    object OnEtNamePlanHasNotError : PlanErrorCleanForm()
    object OnEtPriceHasNotError : PlanErrorCleanForm()
    object OnEtDownloadSpeedHasNotError : PlanErrorCleanForm()
    object OnEtUploadSpeedHasNotError : PlanErrorCleanForm()
}
