package com.dscorp.ispadmin.presentation.ui.features.napboxeslist

import com.example.cleanarchitecture.domain.domain.entity.NapBox

interface OnItemClickListener {
    fun onItemClick(napBox: NapBox)
}
