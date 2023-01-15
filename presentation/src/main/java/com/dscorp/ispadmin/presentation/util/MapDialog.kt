package com.dscorp.ispadmin.presentation.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.DialogMapBinding

/**
 * Created by Sergio Carrillo Diestra on 15/01/2023.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
class MapDialog : DialogFragment() {

    lateinit var binding: DialogMapBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_map, null, true)

        return binding.root
    }

}