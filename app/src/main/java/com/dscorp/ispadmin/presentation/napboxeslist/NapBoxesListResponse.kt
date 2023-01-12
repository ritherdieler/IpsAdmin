package com.dscorp.ispadmin.presentation.napboxeslist

import com.dscorp.ispadmin.repository.model.NapBox
import com.dscorp.ispadmin.repository.model.ServiceOrder
import com.dscorp.ispadmin.repository.model.Subscription

/**
 * Created by Sergio Carrillo Diestra on 19/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class NapBoxesListResponse {
    class OnNapBoxesListFound(val napBoxesList: List<NapBox>) : NapBoxesListResponse()
    class OnError(val error: Exception) : NapBoxesListResponse()

}
