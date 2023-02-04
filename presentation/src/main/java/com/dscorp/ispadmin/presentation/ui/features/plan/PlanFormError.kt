package com.dscorp.ispadmin.presentation.ui.features.plan

/**
 * Created by Sergio Carrillo Diestra on 14/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class PlanFormError(val message: String) {
    companion object {
        const val NAME_ERROR = "Este campo no puede estar vacio"
        const val PRICE_ERROR = "Este campo no puede estar vacio"
        const val DOWNLOAD_SPEED_ERROR = "Este campo no puede estar vacio"
        const val UPLOAD_SPEED_ERROR = "Este campo no puede estar vacio"
    }

    class OnEtNamePlanError : PlanFormError(NAME_ERROR)
    class OnEtPriceError : PlanFormError(PRICE_ERROR)
    class OnEtDowloadSpeedError : PlanFormError(DOWNLOAD_SPEED_ERROR)
    class OnEtUploadSpeedError : PlanFormError(UPLOAD_SPEED_ERROR)
}
