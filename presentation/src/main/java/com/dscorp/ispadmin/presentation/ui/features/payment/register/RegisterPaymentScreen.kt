package com.dscorp.ispadmin.presentation.ui.features.payment.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dscorp.ispadmin.presentation.theme.MyTheme
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyAutoCompleteTextViewCompose
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyButton
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyCustomDialog
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyOutLinedDropDown
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyOutlinedTextField
import com.example.cleanarchitecture.domain.entity.Payment
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPaymentScreen(
    payment: Payment,
    onNavigateBack: () -> Unit,
    viewModel: RegisterPaymentViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(payment) {
        viewModel.onEvent(RegisterPaymentEvent.SetPayment(payment))
    }
    
    if (state.isSuccess) {
        SuccessDialog(onDismiss = onNavigateBack)
    }
    
    MyTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Registrar Pago") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                )
            }
        ) { paddingValues ->
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Debt information card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AttachMoney,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Deuda a pagar",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            
                            Text(
                                text = payment.amountToPayStr(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                    
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Text(
                        text = "Método de pago",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    // Payment Method Dropdown
                    val paymentMethods = listOf("Efectivo", "Tarjeta", "Yape", "Plin", "Transferencia", "Depósito")
                    MyOutLinedDropDown(
                        modifier = Modifier.fillMaxWidth(),
                        items = paymentMethods,
                        selected = state.paymentMethod,
                        label = "Seleccionar método",
                        onItemSelected = { selectedMethod ->
                            viewModel.onEvent(RegisterPaymentEvent.PaymentMethodSelected(selectedMethod))
                        },
                        hasError = state.errorMessages.containsKey("paymentMethod")
                    )
                    
                    if (state.errorMessages.containsKey("paymentMethod")) {
                        Text(
                            text = state.errorMessages["paymentMethod"] ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    // Show Electronic Payer Name field only if payment method is not cash
                    AnimatedVisibility(visible = state.paymentMethod.isNotEmpty() && state.paymentMethod != "Efectivo") {
                        Column {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Datos del pagador",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Electronic Payer Name AutoComplete
                            MyAutoCompleteTextViewCompose(
                                modifier = Modifier.fillMaxWidth(),
                                items = state.electronicPayers,
                                selectedItem = state.electronicPayerName.takeIf { it.isNotEmpty() },
                                label = "Introduce o selecciona un nombre",
                                onItemSelected = { payer ->
                                    viewModel.onEvent(RegisterPaymentEvent.ElectronicPayerNameChanged(payer))
                                },
                                onSelectionCleared = {
                                    viewModel.onEvent(RegisterPaymentEvent.ElectronicPayerNameChanged(null))
                                },
                                errorMessage = state.errorMessages["electronicPayerName"],
                                hasError = state.errorMessages.containsKey("electronicPayerName"),
                                onTextChanged = {
                                    viewModel.onEvent(RegisterPaymentEvent.ElectronicPayerNameChanged(it))
                                }
                            )

                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Discount section
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Discount,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Column {
                                        Text(
                                            text = "Aplicar descuento",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Active esta opción si necesita aplicar un descuento",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                
                                Switch(
                                    checked = state.showDiscountFields,
                                    onCheckedChange = { 
                                        viewModel.onEvent(RegisterPaymentEvent.ToggleDiscountFields)
                                    }
                                )
                            }
                            
                            // Show discount fields if necessary
                            AnimatedVisibility(visible = state.showDiscountFields) {
                                Column {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    // Discount Amount Field
                                    MyOutlinedTextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = state.discountAmount,
                                        label = "Monto de descuento",
                                        errorMessage = state.errorMessages["discountAmount"],
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        onValueChange = { amount ->
                                            viewModel.onEvent(RegisterPaymentEvent.DiscountAmountChanged(amount))
                                        },
                                        hasError = state.errorMessages.containsKey("discountAmount")
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    // Discount Reason Dropdown
                                    val discountReasons = listOf("Fallas de internet", "Fallas de TVcable", "Error de facturación")
                                    MyOutLinedDropDown(
                                        modifier = Modifier.fillMaxWidth(),
                                        items = discountReasons,
                                        selected = state.discountReason,
                                        label = "Razón del descuento",
                                        onItemSelected = { reason ->
                                            viewModel.onEvent(RegisterPaymentEvent.DiscountReasonChanged(reason))
                                        },
                                        hasError = state.errorMessages.containsKey("discountReason")
                                    )
                                    
                                    if (state.errorMessages.containsKey("discountReason")) {
                                        Text(
                                            text = state.errorMessages["discountReason"] ?: "",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Show general error if any
                    if (state.errorMessages.containsKey("general")) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = state.errorMessages["general"] ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Register Payment Button
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Payments,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            
                            Text(
                                text = "Confirmar pago",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 16.dp)
                            )
                            
                            MyButton(
                                text = "Registrar",
                                isLoading = state.isLoading,
                                onClick = {
                                    viewModel.onEvent(RegisterPaymentEvent.RegisterPayment)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessDialog(onDismiss: () -> Unit) {
    MyCustomDialog(
        onDismissRequest = onDismiss,
        cancelable = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Pago registrado",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "El pago ha sido registrado exitosamente.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            MyButton(
                text = "Aceptar",
                modifier = Modifier.fillMaxWidth(),
                onClick = onDismiss
            )
        }
    }
} 