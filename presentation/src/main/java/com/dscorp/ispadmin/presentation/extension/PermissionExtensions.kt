package com.dscorp.ispadmin.presentation.extension

import android.Manifest
import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.presentation.util.PermissionManager

// no funciona porqque la instancia se crea  en un momento del ciclo de vida incorrecto
fun Fragment.requireLocationPermission(
    onSuccess: () -> Unit,
) {
    withGpsEnabled {
        val permissionManager = PermissionManager(
            this,
            onDeny = { openLocationSetting() },
            onRationale = { showLocationRationaleDialog() })
        permissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
            onSuccess.invoke()
        }
    }
}