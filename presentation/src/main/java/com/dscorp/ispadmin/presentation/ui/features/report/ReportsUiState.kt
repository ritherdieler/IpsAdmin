package com.dscorp.ispadmin.presentation.ui.features.report

import com.example.cleanarchitecture.domain.domain.entity.DownloadDocumentResponse

sealed class ReportsUiState {
    class DebtorsSubscriptionsDocument(val document: DownloadDocumentResponse) : ReportsUiState()
    class WithPaymentCommitmentSubscriptionsDocument(val document: DownloadDocumentResponse) : ReportsUiState()
    class SuspendedSubscriptionsDocument(val document: DownloadDocumentResponse) : ReportsUiState()
    class CutOffSubscriptionsDocument(val document: DownloadDocumentResponse) : ReportsUiState()


}
