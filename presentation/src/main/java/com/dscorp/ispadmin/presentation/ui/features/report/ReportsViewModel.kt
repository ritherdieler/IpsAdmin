package com.dscorp.ispadmin.presentation.ui.features.report

import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.delay

class ReportsViewModel(private val repository: IRepository) : BaseViewModel<ReportsUiState>() {

    fun downloadDebtorWithActiveSubscriptionsDocument() = executeWithProgress {
        val response = repository.downloadDebtorWithActiveSubscriptionsDocument()
        uiState.value =
            BaseUiState(ReportsUiState.DocumentReady(response))
        delay(1000)
    }

    fun downloadPaymentCommitmentSubscriptionsDocument() = executeWithProgress {
        val response = repository.downloadWithPaymentCommitmentSubscriptionsDocument()
        uiState.value =
            BaseUiState(ReportsUiState.DocumentReady(response))
        delay(1000)
    }

    fun downloadSuspendedSubscriptionsDocument() = executeWithProgress {
        val response = repository.downloadSuspendedSubscriptionsDocument()
        uiState.value = BaseUiState(ReportsUiState.DocumentReady(response))
        delay(1000)
    }

    fun downloadCutOffSubscriptionsDocument() = executeWithProgress {
        val response = repository.downloadCutOffSubscriptionsDocument()
        uiState.value =
            BaseUiState(ReportsUiState.DocumentReady(response))
        delay(1000)
    }

    fun downloadPastMonthDebtors() = executeWithProgress {
        val response = repository.downloadPastMontSubscriptionsDocument()
        uiState.value =
            BaseUiState(ReportsUiState.DocumentReady(response))
        delay(1000)
    }

    fun downloadCancelledSubscriptionsFromCurrentMonth() = executeWithProgress {
        val response = repository.downloadCancelledSubscriptionFromCurrentMonthDocument()
        uiState.value =
            BaseUiState(ReportsUiState.DocumentReady(response))
        delay(1000)
    }

    fun downloadCancelledSubscriptionsFromPastMonth() = executeWithProgress {
        val response = repository.downloadCancelledSubscriptionsFromPastMonthDocument()
        uiState.value =
            BaseUiState(ReportsUiState.DocumentReady(response))
        delay(1000)
    }

    fun downloadDebtorsCutOffCandidatesSubscriptionsDocument() = executeWithProgress {
        val response = repository.downloadDebtorsCutOffCandidatesSubscriptionsDocument()
        uiState.value =
            BaseUiState(ReportsUiState.DocumentReady(response))
        delay(1000)
    }

    fun downloadDebtorWithCancelledSubscriptionsDocument()= executeWithProgress {
        val response = repository.downloadDebtorWithCancelledSubscriptionsDocument()
        uiState.value =
            BaseUiState(ReportsUiState.DocumentReady(response))
        delay(1000)
    }
}
