package com.dscorp.ispadmin.presentation.ui.features.plan

/**
 * Created by Sergio Carrillo Diestra on 14/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class PlanErrorCleanForm {
    object OnEtNamePlanError : PlanErrorCleanForm()
    object OnEtPriceError : PlanErrorCleanForm()
    object OnEtDownloadSpeedError : PlanErrorCleanForm()
    object OnEtUploadSpeedError : PlanErrorCleanForm()
}
