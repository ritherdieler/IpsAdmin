package com.dscorp.ispadmin.presentation.ui.features.report

import com.example.cleanarchitecture.domain.domain.entity.DownloadDocumentResponse

sealed class ReportsUiState {
    class DocumentReady(val document: DownloadDocumentResponse) : ReportsUiState()
}
