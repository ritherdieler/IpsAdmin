package com.dscorp.ispadmin.presentation.ui.features.subscription.pending

import com.dscorp.ispadmin.domain.model.PendingSubscriptionStatus

data class PendingSubscriptionsUiState(
    val items: List<PendingSubscriptionListItem> = emptyList(),
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
    val failedCount: Int = 0,
    val lastError: String? = null
)

sealed interface PendingSubscriptionsUiEvent {
    data class ShowSuccessSnackbar(val message: String) : PendingSubscriptionsUiEvent
}

data class PendingSubscriptionListItem(
    val localId: String,
    val clientName: String,
    val dni: String,
    val createdAt: Long,
    val status: PendingSubscriptionStatus
)

sealed interface PendingSubscriptionsIntent {
    data object Load : PendingSubscriptionsIntent
    data object Sync : PendingSubscriptionsIntent
    data object ClearError : PendingSubscriptionsIntent
}
