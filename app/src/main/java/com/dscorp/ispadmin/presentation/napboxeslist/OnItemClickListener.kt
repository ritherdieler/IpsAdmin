package com.dscorp.ispadmin.presentation.napboxeslist

import com.dscorp.ispadmin.repository.model.NapBox

interface OnItemClickListener {
        fun onItemClick(napBox: NapBox)
    }

