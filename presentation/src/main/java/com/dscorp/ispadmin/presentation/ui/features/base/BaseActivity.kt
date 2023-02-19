package com.dscorp.ispadmin.presentation.ui.features.base

import androidx.appcompat.app.AppCompatActivity
import com.dscorp.ispadmin.presentation.extension.showCurrentSimpleName
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.android.inject

abstract class BaseActivity : AppCompatActivity() {

    protected val firebaseAnalytics: FirebaseAnalytics by inject()

    override fun onResume() {
        super.onResume()
        showCurrentSimpleName()
    }
}
