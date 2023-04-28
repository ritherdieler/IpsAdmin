package com.dscorp.ispadmin.presentation.ui.features.ippool.seeip

import com.example.cleanarchitecture.domain.domain.entity.Ip

sealed class IpListUiState {
    class IpListReady(val ips: List<Ip>) : IpListUiState()

}
