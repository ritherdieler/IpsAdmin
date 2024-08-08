package com.dscorp.ispadmin.presentation.ui.features.payment.payerFinder.compose

import MyTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dscorp.ispadmin.presentation.theme.Primary
import com.dscorp.ispadmin.presentation.theme.Secondary
import com.example.cleanarchitecture.domain.domain.entity.extensions.PayerFinderResult
import myTypography

@Composable
fun PayerFinder(
    modifier: Modifier = Modifier,
    results: List<PayerFinderResult>,
    onTextChanged: (String) -> Unit = {}
) {

    var text by remember { mutableStateOf("") }

    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)) {

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            label = { Text("Buscar pagador") },
            onValueChange = {
                text = it
                onTextChanged(it)
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(results) { item ->
                Text(
                    text = "Pagador:${item.electronicPayerName.uppercase()}",
                    style = myTypography.titleMedium,
                    color = Primary
                )
                Text(
                    text = "Cliente: ${item.subscriptionName}",
                    style = myTypography.titleSmall,
                    color = Secondary
                )
                HorizontalDivider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PayerFinderPreview() {
    MyTheme {
        PayerFinder(
            results = listOf(
                PayerFinderResult(
                    subscriptionId = 1,
                    subscriptionName = "Cliente 1",
                    electronicPayerName = "Pagador 1",
                    paymentMethod = "Efectivo",
                    paymentDate = "01/01/2022",
                    amountPaid = 100.0
                ),
                PayerFinderResult(
                    subscriptionId = 2,
                    subscriptionName = "Cliente 2",
                    electronicPayerName = "Pagador 2",
                    paymentMethod = "Tarjeta",
                    paymentDate = "02/01/2022",
                    amountPaid = 200.0
                ),
                PayerFinderResult(
                    subscriptionId = 3,
                    subscriptionName = "Cliente 3",
                    electronicPayerName = "Pagador 3",
                    paymentMethod = "Transferencia",
                    paymentDate = "03/01/2022",
                    amountPaid = 300.0
                )
            )
        )
    }

}