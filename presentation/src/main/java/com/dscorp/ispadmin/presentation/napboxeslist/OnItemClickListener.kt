package com.dscorp.ispadmin.presentation.napboxeslist

import com.example.cleanarchitecture.domain.domain.entity.NapBox

interface OnItemClickListener {
        fun onItemClick(napBox: NapBox)
    }

