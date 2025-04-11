package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dscorp.ispadmin.presentation.ui.features.dialog.MyConfirmDialog
import com.dscorp.ispadmin.presentation.ui.features.dialog.MyCustomDialog
import com.dscorp.ispadmin.presentation.ui.features.migration.Loader
import com.dscorp.ispadmin.presentation.ui.features.migration.MigrationActivity
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.SubscriptionFinderFragmentDirections
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.CANCEL_SUBSCRIPTION
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.CHANGE_NAP_BOX
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.EDIT_PLAN_SUBSCRIPTION
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.MIGRATE_TO_FIBER
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.SEE_DETAILS
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.SHOW_PAYMENT_HISTORY
import com.example.cleanarchitecture.domain.entity.NapBoxResponse
import com.example.cleanarchitecture.domain.entity.PlaceResponse
import com.example.cleanarchitecture.domain.entity.SubscriptionResume
import com.example.data2.data.apirequestmodel.MoveOnuRequest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

const val SUBSCRIPTION_ID = "subscriptionId"

/**
 * Main screen for finding and managing subscriptions.
 */
@Composable
fun SubscriptionFinderScreen(
    navController: NavController,
    viewModel: SubscriptionFinderViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutinesScope = rememberCoroutineScope()
    var showCancelSubscriptionConfirmDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showChangeNapBoxDialog by remember { mutableStateOf(false) }
    
    // Track which subscription card is expanded
    var expandedSubscriptionId by remember { mutableStateOf<Int?>(null) }

    // Main subscription finder content
    SubscriptionFinder(
        subscriptions = uiState.subscriptions,
        onSearch = {
            coroutinesScope.launch {
                viewModel.documentNumberFlow.emit(it)
            }
        },
        onMenuItemSelected = { menuItem, subscription ->
            handleMenuAction(
                menuItem = menuItem,
                subscription = subscription,
                navController = navController,
                context = context,
                viewModel = viewModel,
                onShowCancelDialog = { showCancelSubscriptionConfirmDialog = true },
                onShowChangeNapBoxDialog = { showChangeNapBoxDialog = true }
            )
        },
        onSubscriptionExpanded = { subscription, expanded ->
            // Handle the expanded state change
            expandedSubscriptionId = if (expanded) subscription.id else null
            
            // Initialize customer form data when expanding
            if (expanded) {
                // Set the selected subscription first
                viewModel.setSelectedSubscription(subscription)
                
                // Then initialize the form data for this subscription
                viewModel.initCustomerFormData(subscription)
            }
        },
        expandedSubscriptionId = expandedSubscriptionId,
        customerFormData = uiState.customerFormData,
        placesState = uiState.placesState,
        saveState = uiState.saveSubscriptionState,
        onFieldChange = viewModel::updateCustomerFormField,
        onPlaceSelected = viewModel::onPlaceSelected,
        onUpdatePlaceId = viewModel::updateCustomerPlaceId,
        onSaveCustomer = viewModel::saveCustomerData
    )

    // Handle cancel subscription state
    HandleCancelSubscriptionState(
        cancelState = uiState.cancelSubscriptionState,
        context = context,
        selectedSubscription = uiState.selectedSubscription,
        onRemoveSubscription = viewModel::removeSubscriptionFromList
    )

    // Cancel subscription confirmation dialog
    if (showCancelSubscriptionConfirmDialog) {
        CancelSubscriptionDialog(
            onDismiss = { showCancelSubscriptionConfirmDialog = false },
            onConfirm = {
                uiState.selectedSubscription?.id?.let {
                    viewModel.cancelSubscription(it)
                }
            }
        )
    }

    // Change NAP box dialog
    if (showChangeNapBoxDialog) {
        ChangeNapBoxDialog(
            viewModel = viewModel,
            selectedSubscription = uiState.selectedSubscription,
            napBoxesState = uiState.napBoxesState,
            context = context,
            onDismiss = { showChangeNapBoxDialog = false }
        )
    }
}

/**
 * Handles menu actions for subscription items
 */
private fun handleMenuAction(
    menuItem: SubscriptionMenu,
    subscription: SubscriptionResume,
    navController: NavController,
    context: Context,
    viewModel: SubscriptionFinderViewModel,
    onShowCancelDialog: () -> Unit,
    onShowChangeNapBoxDialog: () -> Unit
) {
    when (menuItem) {
        SHOW_PAYMENT_HISTORY -> {
            navController.navigate(
                SubscriptionFinderFragmentDirections.findSubscriptionToPaymentHistoryFragment(
                    subscription.id
                )
            )
        }

        EDIT_PLAN_SUBSCRIPTION -> {
            navController.navigate(
                SubscriptionFinderFragmentDirections.findSubscriptionToEditSubscriptionFragment(
                    subscription.id
                )
            )
        }

        SEE_DETAILS -> {
            navController.navigate(
                SubscriptionFinderFragmentDirections.findSubscriptionToSubscriptionDetail(
                    subscription.id
                )
            )
        }

        MIGRATE_TO_FIBER -> {
            val intent = Intent(context, MigrationActivity::class.java)
            intent.putExtra(SUBSCRIPTION_ID, subscription.id)
            context.startActivity(intent)
        }

        CANCEL_SUBSCRIPTION -> {
            viewModel.setSelectedSubscription(subscription)
            onShowCancelDialog()
        }

        CHANGE_NAP_BOX -> {
            viewModel.setSelectedSubscription(subscription)
            onShowChangeNapBoxDialog()
        }
    }
}

/**
 * Component to handle cancel subscription state changes
 */
@Composable
private fun HandleCancelSubscriptionState(
    cancelState: CancelSubscriptionState,
    context: Context,
    selectedSubscription: SubscriptionResume?,
    onRemoveSubscription: (Int) -> Unit
) {
    when (cancelState) {
        CancelSubscriptionState.Empty -> {}
        CancelSubscriptionState.Error -> {
            Toast.makeText(
                context,
                "Ocurrió un error al cancelar la suscripción",
                Toast.LENGTH_LONG
            ).show()
        }

        CancelSubscriptionState.Loading -> Loader(Modifier.background(color = Color.White))
        CancelSubscriptionState.Success -> {
            Toast.makeText(context, "Suscripción cancelada correctamente", Toast.LENGTH_LONG)
                .show()
            selectedSubscription?.id?.let { onRemoveSubscription(it) }
        }
    }
}

/**
 * Dialog to confirm subscription cancellation
 */
@Composable
private fun CancelSubscriptionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    MyConfirmDialog(
        title = "Cancelar suscripción",
        body = {
            Text(text = "¿Está seguro de cancelar la suscripción?")
        },
        onDismissRequest = onDismiss,
        onAccept = onConfirm
    )
}

/**
 * Dialog to change NAP box - simplified version
 */
@Composable
private fun ChangeNapBoxDialog(
    viewModel: SubscriptionFinderViewModel,
    selectedSubscription: SubscriptionResume?,
    napBoxesState: NapBoxesState,
    context: Context,
    onDismiss: () -> Unit
) {
    // Fetch NAP boxes when dialog is shown
    viewModel.getNapBoxes()

    if (selectedSubscription == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "No hay suscripción seleccionada", Toast.LENGTH_LONG).show()
            onDismiss()
        }
        return
    }

    MyCustomDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Cambiar NAP Box",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            when (napBoxesState) {
                is NapBoxesState.Error -> {
                    Text(text = "Error al cargar los NAP BOX")
                    Button(onClick = onDismiss) {
                        Text("Cerrar")
                    }
                }

                is NapBoxesState.Loading -> {
                    CircularProgressIndicator()
                    Text(text = "Cargando...")
                }

                is NapBoxesState.NapBoxListLoaded -> {
                    val currentNapBoxCode = selectedSubscription.napBox?.code ?: "Sin asignar"
                    var selectedNapBoxIndex by remember { mutableStateOf(-1) }
                    
                    // Current NAP Box
                    OutlinedTextField(
                        value = "NAP Box actual: $currentNapBoxCode",
                        onValueChange = { },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Simple dropdown with list of NAP Box options
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Seleccione nuevo NAP Box:", 
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        
                        // Display options as a list of buttons
                        napBoxesState.items.forEachIndexed { index, napBox ->
                            if (napBox.code != currentNapBoxCode) {
                                Button(
                                    onClick = { selectedNapBoxIndex = index },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = if (selectedNapBoxIndex == index) {
                                        MaterialTheme.colorScheme.primary.let {
                                            ButtonDefaults.buttonColors(containerColor = it)
                                        }
                                    } else {
                                        ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }
                                ) {
                                    Text("${napBox.code} - ${napBox.address}")
                                }
                            }
                        }
                    }
                    
                    // Confirm button
                    Button(
                        onClick = {
                            if (selectedNapBoxIndex >= 0) {
                                val selectedNapBox = napBoxesState.items[selectedNapBoxIndex]
                                val request = MoveOnuRequest(
                                    subscriptionId = selectedSubscription.id,
                                    newNapBoxId = selectedNapBox.id!!.toInt()
                                )
                                viewModel.changeNapBox(request)
                            }
                        },
                        enabled = selectedNapBoxIndex >= 0,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirmar cambio")
                    }
                }

                is NapBoxesState.NapBoxChanged -> {
                    Text(text = "NAP Box cambiado exitosamente")
                    Button(onClick = { 
                        onDismiss()
                        viewModel.resetNapBoxFlow()
                    }) {
                        Text("Cerrar")
                    }
                    LaunchedEffect(Unit) {
                        Toast.makeText(context, "NAP Box cambiado exitosamente", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
