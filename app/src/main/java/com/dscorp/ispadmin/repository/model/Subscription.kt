package com.dscorp.ispadmin.repository.model

/**
 * Created by Sergio Carrillo Diestra on 30/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
data class Subscription(
    var code:Int? = null,
    var firstName: String,
    var lastName: String,
    var dni: String,
    var password:String,
    var address:String,
    var phone:String,
    var subscriptionDate:Int,
    var isNew:Boolean? = null,
    var serviceIsSuspended: Boolean? = null

)
