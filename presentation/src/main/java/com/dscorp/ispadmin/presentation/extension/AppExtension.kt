package com.dscorp.ispadmin.presentation.extension

import androidx.navigation.NavController


fun NavController.navigateSafe(destinationId: Int) {
    try {
        navigate(destinationId)
    } catch (e: Exception) {
        e.printStackTrace()
    }

}