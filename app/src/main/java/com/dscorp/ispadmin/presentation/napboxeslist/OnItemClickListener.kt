package com.dscorp.ispadmin.presentation.napboxeslist

import com.dscorp.ispadmin.domain.entity.NapBox

interface OnItemClickListener {
        fun onItemClick(napBox: NapBox)
    }

