package com.dscorp.ispadmin.presentation.ui.features.mufas

import com.example.cleanarchitecture.domain.domain.entity.Mufa

sealed class MufaUiState {

    class OnMufasListFound(val mufasList: List<Mufa>) : MufaUiState()
}
