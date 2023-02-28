package com.dscorp.ispadmin.presentation.ui.features.serviceorder.history

import android.view.View
import com.example.cleanarchitecture.domain.domain.entity.ServiceOrderResponse

interface OnItemServiceOrderClickListener {
    fun onServiceOrderPopupButtonSelected(serviceOrderResponse: ServiceOrderResponse, view: View)

}
