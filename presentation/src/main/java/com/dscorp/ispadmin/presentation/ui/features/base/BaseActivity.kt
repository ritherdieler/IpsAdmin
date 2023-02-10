package com.dscorp.ispadmin.presentation.ui.features.base

import androidx.appcompat.app.AppCompatActivity
import com.dscorp.ispadmin.presentation.extension.showCurrentSimpleName

abstract class BaseActivity:AppCompatActivity() {


    override fun onResume() {
        super.onResume()
        showCurrentSimpleName()
    }
}