package com.dscorp.ispadmin.presentation.ui.features.napboxeslist

import android.view.View
import com.example.cleanarchitecture.domain.domain.entity.NapBoxResponse

interface OnItemClickListener {
    fun onItemClick(napBox: NapBoxResponse)
    fun onNapBoxPopupButtonSelected(napbox: NapBoxResponse, view: View)

}
