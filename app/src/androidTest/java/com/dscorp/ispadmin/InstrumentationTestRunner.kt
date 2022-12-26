package com.dscorp.ispadmin

import android.app.Application
import androidx.test.runner.AndroidJUnitRunner

/**
 * Created by Sergio Carrillo Diestra on 25/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 */
class InstrumentationTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        classLoader: ClassLoader?,
        className: String?,
        context: android.content.Context?,
    ): Application? {
        return super.newApplication(classLoader, KoinAppForInstrumentation::class.java.name, context)
    }
}