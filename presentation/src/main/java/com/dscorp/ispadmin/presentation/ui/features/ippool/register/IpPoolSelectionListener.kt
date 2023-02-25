package com.dscorp.ispadmin.presentation.ui.features.ippool.register

import com.example.cleanarchitecture.domain.domain.entity.IpPool

interface IpPoolSelectionListener {

    fun onIpPoolSelected(ipPool:IpPool)
}