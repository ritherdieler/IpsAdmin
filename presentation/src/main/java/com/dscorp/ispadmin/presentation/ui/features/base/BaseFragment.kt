package com.dscorp.ispadmin.presentation.ui.features.base

import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.presentation.extension.showCurrentSimpleName

abstract class BaseFragment:Fragment() {


    override fun onResume() {
        super.onResume()
       showCurrentSimpleName()
    }

}