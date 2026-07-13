package com.dscorp.ispadmin.presentation.ui.features.mufas

import com.dscorp.ispadmin.data.repository.IRepository
import com.dscorp.ispadmin.observability.ObservabilityClient
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import org.koin.core.component.KoinComponent

class MufaViewModel(
    val repository: IRepository,
    private val observabilityClient: ObservabilityClient
) : BaseViewModel<MufaUiState>(), KoinComponent {

    private companion object {
        const val OBS_FEATURE = "mufa"
        const val OBS_SCREEN = "mufa"
    }

    init {
        getMufas()
    }

    private fun getMufas() = executeWithProgress {
        try {
            val mufas = repository.getMufas()
            uiState.value = BaseUiState( MufaUiState.OnMufasListFound(mufas))
        } catch (e: Exception) {
            observabilityClient.reportError(
                throwable = e,
                message = "Fallo al cargar lista de mufas",
                tags = mapOf("feature" to OBS_FEATURE, "screen" to OBS_SCREEN, "action" to "load_mufas")
            )
            throw e
        }
    }
}