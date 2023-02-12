package com.dscorp.ispadmin.presentation.ui.features.base

import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.presentation.extension.analytics.sendScreen
import com.dscorp.ispadmin.presentation.extension.showCurrentSimpleName
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.android.inject

abstract class BaseFragment : Fragment() {
    protected val firebaseAnalytics: FirebaseAnalytics by inject()

    override fun onResume() {
        super.onResume()
        showCurrentSimpleName()
        firebaseAnalytics.sendScreen(this::class.java.simpleName)

    }

}