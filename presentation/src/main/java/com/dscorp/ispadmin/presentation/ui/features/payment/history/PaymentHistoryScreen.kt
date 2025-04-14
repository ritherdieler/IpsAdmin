package com.dscorp.ispadmin.presentation.ui.features.payment.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.theme.MyTheme
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyButton
import com.example.cleanarchitecture.domain.entity.Payment
import kotlinx.coroutines.launch

@Composable
fun PaymentHistoryScreen(
    viewModel: PaymentHistoryViewModel,
    onPaymentItemClicked: (Payment) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // States for UI components
    var onlyPendingChecked by remember { mutableStateOf(false) }

    // Show snackbar for errors and success messages
    LaunchedEffect(state.error, state.isServiceReactivated) {
        state.error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
            }
        }

        if (state.isServiceReactivated) {
            val message = "El servicio fue reactivado con exito"
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.filter),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Checkbox(
                        checked = onlyPendingChecked,
                        onCheckedChange = { isChecked ->
                            onlyPendingChecked = isChecked
                            if (isChecked) {
                                viewModel.showOnlyPendingPayments()
                            } else {
                                viewModel.showAllPayments()
                            }
                        }
                    )
                    Text(
                        text = stringResource(R.string.only_pending),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                if (!onlyPendingChecked) {
                    Text(
                        text = stringResource(
                            R.string.disclaimer_payment_list,
                            state.payments.size
                        ),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Payments list
                if (state.isLoading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(state.payments) { payment ->
                            PaymentItem(
                                payment = payment,
                                onClick = { onPaymentItemClicked(payment) }
                            )
                        }
                    }
                }


                // Reactivate service button if needed
                if (viewModel.subscriptionId != null) {
                    Spacer(modifier = Modifier.height(16.dp))


                    // Reactivate service button if needed
                    if (viewModel.subscriptionId != null) {
                        Spacer(modifier = Modifier.height(16.dp))

                        MyButton(
                            onClick = { viewModel.reactivateService() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isReactivationButtonLoading,
                            text = stringResource(R.string.reactivate_service),
                            isLoading = state.isReactivationButtonLoading
                        )
                    }
                }
            }


        }
    }
}

@Composable
fun PaymentItem(
    payment: Payment,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = payment.billingDateStr(),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = payment.amountToPayStr(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = payment.paidStatusStr(),
                style = MaterialTheme.typography.bodyMedium,
                color = if (payment.paid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentItemPreview() {
    val payment = Payment(
        id = 1,
        billingDate = System.currentTimeMillis(),
        amountToPay = 100.0,
        paid = false
    )

    MyTheme {
        PaymentItem(payment = payment)
    }
} 