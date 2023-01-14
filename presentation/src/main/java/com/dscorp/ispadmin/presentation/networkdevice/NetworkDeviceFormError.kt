package com.dscorp.ispadmin.presentation.networkdevice

/**
 * Created by Sergio Carrillo Diestra on 16/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
sealed class NetworkDeviceFormError{
    class OnEtNameError(val error:String):NetworkDeviceFormError()
    class OnEtPassword(val error: String):NetworkDeviceFormError()
    class OnEtUserName(val error: String):NetworkDeviceFormError()
    class OnEtAddress(val error: String):NetworkDeviceFormError()

}
