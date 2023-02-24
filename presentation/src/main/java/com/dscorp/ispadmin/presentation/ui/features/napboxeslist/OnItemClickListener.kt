package com.dscorp.ispadmin.presentation.ui.features.napboxeslist

import android.view.View
import com.example.cleanarchitecture.domain.domain.entity.NapBox
import com.example.cleanarchitecture.domain.domain.entity.NapBoxResponse

interface OnItemClickListener {
    fun onItemClick(napBox: NapBox)
/*
    fun onSubscriptionPopupButtonSelected(napbox: NapBoxResponse, view: View)
*/

}
