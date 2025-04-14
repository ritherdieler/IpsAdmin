package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyButton
import com.dscorp.ispadmin.presentation.ui.features.dialog.MyConfirmDialog
import com.dscorp.ispadmin.presentation.ui.features.dialog.MyCustomDialog
import com.dscorp.ispadmin.presentation.ui.features.locationMapView.MAP_SELECTION_REQUEST_KEY
import com.dscorp.ispadmin.presentation.ui.features.locationMapView.MAP_SELECTION_RESULT_KEY
import com.dscorp.ispadmin.presentation.ui.features.migration.Loader
import com.dscorp.ispadmin.presentation.ui.features.migration.MigrationActivity
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.SubscriptionFinderFragmentDirections
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.CANCEL_SUBSCRIPTION
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.CHANGE_NAP_BOX
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.EDIT_PLAN_SUBSCRIPTION
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.MIGRATE_TO_FIBER
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.SEE_DETAILS
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.SHOW_PAYMENT_HISTORY
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.UPDATE_LOCATION
import com.example.cleanarchitecture.domain.entity.GeoLocation
import com.example.cleanarchitecture.domain.entity.SubscriptionResume
import com.example.data2.data.apirequestmodel.MoveOnuRequest
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val SUBSCRIPTION_ID = "subscriptionId"

/**
 * Main screen for finding and managing subscriptions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionFinderScreen(
    navController: NavController,
    viewModel: SubscriptionFinderViewModel = koinViewModel(),
    onShowMapSelector: (GeoLocation?) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutinesScope = rememberCoroutineScope()
    var showCancelSubscriptionConfirmDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showChangeNapBoxDialog by remember { mutableStateOf(false) }

    // Track which subscription card is expanded
    var expandedSubscriptionId by remember { mutableStateOf<Int?>(null) }

    // For scrolling behavior with topAppBar
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()


    // Listen for the result from the map dialog
    LaunchedEffect(lifecycleOwner) {
        val activity = context as? FragmentActivity
        activity?.supportFragmentManager?.setFragmentResultListener(
            MAP_SELECTION_REQUEST_KEY,
            lifecycleOwner
        ) { _, bundle ->
            val result = bundle.getParcelable<LatLng>(MAP_SELECTION_RESULT_KEY)
            result?.let {
                viewModel.updateCoordinatesFromMap(it)
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search card with nice material design
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Buscar suscripción",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val filters = filters
                    var selectedFilter by remember { mutableStateOf(filters[0]) }
                    var searchQuery by remember { mutableStateOf("") }
                    var lastNameQuery by remember { mutableStateOf("") }

                    // Show filter tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filters.forEach { filter ->
                            val isSelected = selectedFilter == filter
                            FilterChip(
                                filter = filter,
                                isSelected = isSelected,
                                onClick = {
                                    // Restablecer los campos de búsqueda al cambiar de filtro
                                    if (selectedFilter != filter) {
                                        searchQuery = ""
                                        lastNameQuery = ""
                                    }
                                    selectedFilter = filter
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Contenido específico según el tipo de filtro
                    when (selectedFilter) {
                        is SubscriptionFilter.BY_NAME -> {
                            // Formulario con dos campos: nombre y apellido
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Campo de nombre
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { newValue ->
                                        searchQuery = newValue
                                        // Realizar búsqueda mientras se escribe en el campo de nombre
                                        coroutinesScope.launch {
                                            viewModel.documentNumberFlow.emit(
                                                SubscriptionFilter.BY_NAME(
                                                    name = newValue,
                                                    lastName = lastNameQuery
                                                )
                                            )
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    label = { Text("Nombre") },
                                    placeholder = { Text("Ingrese nombre") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )

                                // Campo de apellido
                                OutlinedTextField(
                                    value = lastNameQuery,
                                    onValueChange = { newValue ->
                                        lastNameQuery = newValue
                                        // Realizar búsqueda mientras se escribe en el campo de apellido
                                        coroutinesScope.launch {
                                            viewModel.documentNumberFlow.emit(
                                                SubscriptionFilter.BY_NAME(
                                                    name = searchQuery,
                                                    lastName = newValue
                                                )
                                            )
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    label = { Text("Apellido") },
                                    placeholder = { Text("Ingrese apellido") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }

                        is SubscriptionFilter.BY_DOCUMENT -> {
                            // Campo único para documento
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { newValue ->
                                    searchQuery = newValue
                                    // Realizar búsqueda mientras se escribe
                                    coroutinesScope.launch {
                                        viewModel.documentNumberFlow.emit(
                                            SubscriptionFilter.BY_DOCUMENT(
                                                newValue
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("DNI") },
                                placeholder = { Text("Ingrese número de documento") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = "Buscar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        is SubscriptionFilter.BY_DATE -> {
                            // Reemplazamos la implementación por completo
                            DateRangeSelector(
                                onSearch = { startDate, endDate ->
                                    coroutinesScope.launch {
                                        viewModel.documentNumberFlow.emit(
                                            SubscriptionFilter.BY_DATE(startDate, endDate)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Results section
            if (uiState.cancelSubscriptionState == CancelSubscriptionState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (uiState.subscriptions.isEmpty()) {
                // No results found
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No se encontraron resultados",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Intente con otros criterios de búsqueda",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                // Main subscription finder content
                SubscriptionFinder(
                    subscriptions = uiState.subscriptions,
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
            }
        }
    }

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

    // Location update dialog - controlled by ViewModel state
    if (uiState.showLocationUpdateDialog) {
        LocationUpdateDialog(
            viewModel = viewModel,
            selectedSubscription = uiState.selectedSubscription,
            saveState = uiState.saveSubscriptionState,
            latitude = uiState.editableLatitude,
            longitude = uiState.editableLongitude,
            onShowMap = { onShowMapSelector(uiState.selectedSubscription?.location) },
            onDismiss = { viewModel.toggleLocationUpdateDialog(false) }
        )
    }
}

@Composable
fun FilterChip(
    filter: SubscriptionFilter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = filter.valueName,
            color = contentColor,
            style = MaterialTheme.typography.labelMedium
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

        UPDATE_LOCATION -> {
            viewModel.setSelectedSubscription(subscription)
            viewModel.toggleLocationUpdateDialog(true)
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
 * Dialog to change NAP box - enhanced version with Material 3 styling
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Simple dropdown with list of NAP Box options
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Seleccione nuevo NAP Box:",
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
                                        ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    } else {
                                        ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("${napBox.code} - ${napBox.address}")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm button
                    MyButton(
                        text = "Confirmar cambio",
                        isLoading = false,
                        enabled = selectedNapBoxIndex >= 0,
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
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                is NapBoxesState.NapBoxChanged -> {
                    Icon(
                        imageVector = Icons.Outlined.CloudSync,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "NAP Box cambiado exitosamente",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MyButton(
                        text = "Aceptar",
                        onClick = {
                            onDismiss()
                            viewModel.resetNapBoxFlow()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    LaunchedEffect(Unit) {
                        Toast.makeText(context, "NAP Box cambiado exitosamente", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }
}

/**
 * Componente para seleccionar un rango de fechas para búsqueda
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelector(
    onSearch: (String, String) -> Unit
) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // Función para formatear la fecha
    fun formatDate(millis: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = millis
        }
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Rango de fechas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Campo de fecha inicial
            OutlinedTextField(
                value = startDate,
                onValueChange = { },
                modifier = Modifier
                    .weight(1f)
                    .clickable { showStartDatePicker = true },
                label = { Text("Fecha inicial") },
                placeholder = { Text("Seleccione") },
                readOnly = true,
                enabled = false,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Seleccionar fecha inicial",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                shape = RoundedCornerShape(8.dp)
            )

            // Campo de fecha final
            OutlinedTextField(
                value = endDate,
                onValueChange = { },
                modifier = Modifier
                    .weight(1f)
                    .clickable { showEndDatePicker = true },
                label = { Text("Fecha final") },
                placeholder = { Text("Seleccione") },
                readOnly = true,
                enabled = false,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Seleccionar fecha final",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                shape = RoundedCornerShape(8.dp)
            )
        }

        // Botón de búsqueda
        Button(
            onClick = {
                if (startDate.isNotEmpty() || endDate.isNotEmpty()) {
                    onSearch(startDate, endDate)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = startDate.isNotEmpty() || endDate.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Buscar",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }

    // Diálogo de selección de fecha inicial
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }

        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            startDate = formatDate(it)
                        }
                        showStartDatePicker = false
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Diálogo de selección de fecha final
    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }

        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            endDate = formatDate(it)
                        }
                        showEndDatePicker = false
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
