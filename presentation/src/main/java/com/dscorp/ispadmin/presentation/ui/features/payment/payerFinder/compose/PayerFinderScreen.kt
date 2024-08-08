package com.dscorp.ispadmin.presentation.ui.features.payment.payerFinder.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.dscorp.ispadmin.presentation.ui.features.payment.register.RegisterPaymentViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PayerFinderScreen(
    modifier: Modifier = Modifier,
    paymentViewModel: RegisterPaymentViewModel = koinViewModel()
) {
    val subscriptions by paymentViewModel.subscriptionFlow.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold { paddingValues ->
        PayerFinder(
            results = subscriptions,
            onTextChanged = {
                coroutineScope.launch {
                    paymentViewModel.paymentSearchFlow.emit(it)
                }
            }
        )
    }
}