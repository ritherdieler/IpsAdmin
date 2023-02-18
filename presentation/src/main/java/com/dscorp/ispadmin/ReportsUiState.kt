package com.dscorp.ispadmin

import com.example.cleanarchitecture.domain.domain.entity.DownloadDocumentResponse

sealed class ReportsUiState {
    class DebtorsDocument(val document: DownloadDocumentResponse) : ReportsUiState()
    class DebtorsDocumentError(val error: String?) : ReportsUiState()

}
