package com.dscorp.ispadmin.presentation.serviceorderlist

import com.dscorp.ispadmin.domain.entity.ServiceOrder

/**
 * Created by Sergio Carrillo Diestra on 19/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class ServicesOrderListResponse{
    class OnServicesOrderListFound(val servicesOrderList :List<ServiceOrder>):ServicesOrderListResponse()
    class OnError(val error:Exception):ServicesOrderListResponse()

}
