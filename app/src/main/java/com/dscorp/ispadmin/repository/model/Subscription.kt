package com.dscorp.ispadmin.repository.model

import java.net.IDN

/**
 * Created by Sergio Carrillo Diestra on 30/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
data class Subscription(
    var id: String? = null,
    var code:String? = null,
    var firstName: String,
    var lastName: String,
    var dni: String,
    var password:String,
    var address:String,
    var phone:String,
    var subscriptionDate:Int,
    var isNew:Boolean? = null,
    var serviceIsSuspended: Boolean? = null,
    var planId:String,
    var networkDeviceId: String

)
