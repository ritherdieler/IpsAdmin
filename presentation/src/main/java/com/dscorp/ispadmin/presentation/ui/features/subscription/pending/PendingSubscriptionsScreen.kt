package com.dscorp.ispadmin.presentation.ui.features.subscription.pending

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dscorp.ispadmin.presentation.theme.MyTheme
import com.dscorp.ispadmin.presentation.ui.components.Loader
import com.dscorp.ispadmin.presentation.ui.components.MyButton
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.SuccessSnackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun PendingSubscriptionsScreen(
    uiState: PendingSubscriptionsUiState,
    onIntent: (PendingSubscriptionsIntent) -> Unit,
    modifier: Modifier = Modifier,
    uiEvents: Flow<PendingSubscriptionsUiEvent> = emptyFlow()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var successMessage by remember { mutableStateOf("") }

    LaunchedEffect(uiEvents) {
        uiEvents.collect { event ->
            when (event) {
                is PendingSubscriptionsUiEvent.ShowSuccessSnackbar -> {
                    successMessage = event.message
                }
            }
        }
    }

    MyTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.testTag("pending_subscriptions_snackbar")
                )
            }
        ) { padding ->
            SuccessSnackbar(
                snackbarHostState = snackbarHostState,
                successMessage = successMessage,
                onDismiss = { successMessage = "" }
            )
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                color = MaterialTheme.colorScheme.background
            ) {
                if (uiState.isLoading) {
                    Loader()
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        MyButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_sync_pending_subscriptions"),
                            text = "Sincronizar suscripciones",
                            enabled = !uiState.isSyncing,
                            isLoading = uiState.isSyncing,
                            onClick = { onIntent(PendingSubscriptionsIntent.Sync) }
                        )
                        uiState.errorMessage?.let { error ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        if (uiState.items.isEmpty()) {
                            Text(
                                text = "No hay suscripciones pendientes",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(uiState.items, key = { it.localId }) { item ->
                                    PendingSubscriptionCard(item = item)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PendingSubscriptionCard(item: PendingSubscriptionListItem) {
    val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        .format(Date(item.createdAt))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.clientName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "DNI: ${item.dni}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = item.status.name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
