package com.dscorp.ispadmin.presentation.ui.features.report

import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.delay

class ReportsViewModel(private val repository: IRepository) :
    BaseViewModel<ReportsUiState>() {

    fun downloadDebtorSubscriptionsDocument() = executeWithProgress {

        val response = repository.downloadDebtorSubscriptionsDocument()
        uiState.value =
            BaseUiState(ReportsUiState.DebtorsSubscriptionsDocument(response))
        delay(1000)
    }

    fun downloadPaymentCommitmentSubscriptionsDocument() = executeWithProgress {
        val response = repository.downloadWithPaymentCommitmentSubscriptionsDocument()
        uiState.value =
            BaseUiState(ReportsUiState.WithPaymentCommitmentSubscriptionsDocument(response))
        delay(1000)
    }

    fun downloadSuspendedSubscriptionsDocument() = executeWithProgress {
        val response = repository.downloadSuspendedSubscriptionsDocument()
        uiState.value = BaseUiState(ReportsUiState.SuspendedSubscriptionsDocument(response))
        delay(1000)
    }

    fun downloadCutOffSubscriptionsDocument() = executeWithProgress {
        val response = repository.downloadCutOffSubscriptionsDocument()
        uiState.value =
            BaseUiState(ReportsUiState.CutOffSubscriptionsDocument(response))
        delay(1000)
    }

    fun downloadPastMonthDebtors() = executeWithProgress {
        val response = repository.downloadPastMontSubscriptionsDocument()
        uiState.value =
            BaseUiState(ReportsUiState.CutOffSubscriptionsDocument(response))
        delay(1000)
    }
}
