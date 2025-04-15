package com.dscorp.ispadmin.presentation.ui.features.supportTicket.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyAutoCompleteTextViewCompose
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyButton
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyCustomDialog
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyOutLinedDropDown
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyOutlinedTextField
import kotlinx.coroutines.delay

@Composable
fun CreateSupportTicketScreen(
    viewModel: CreateSupportTicketViewModel,
    onTicketCreated: () -> Unit
) {
    // Recoger el estado de la UI
    val uiState by viewModel.uiState.collectAsState()

    // Variable para la búsqueda con debounce
    var searchText by remember { mutableStateOf("") }

    // Observar el estado para manejar eventos
    LaunchedEffect(uiState.isTicketCreated) {
        if (uiState.isTicketCreated) {
            viewModel.resetTicketCreated()
            onTicketCreated()
        }
    }

    // Manejar la búsqueda con debounce
    LaunchedEffect(searchText) {
        if (searchText.isNotEmpty()) {
            delay(500) // Debounce de 500ms
            viewModel.findSubscriptionByNames(searchText)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = stringResource(R.string.create_support_tickets),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 24.dp)
                )
            }
        }
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
                    .verticalScroll(rememberScrollState()),
            ) {
                // Tarjeta principal
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Información de Contacto",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Campo de teléfono con icono
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Phone,
                                contentDescription = "Teléfono",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            MyOutlinedTextField(
                                value = uiState.phone,
                                onValueChange = { if (it.length <= 9) viewModel.updatePhone(it) },
                                label = "Teléfono de contacto",
                                modifier = Modifier.fillMaxWidth(),
                                hasError = uiState.phoneError != null,
                                errorMessage = uiState.phoneError,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Detalles del Problema",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
                        )

                        // Campo de categoría con icono
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Category,
                                contentDescription = "Categoría",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            // Añadir texto de error debajo del dropdown si es necesario
                            Column(modifier = Modifier.fillMaxWidth()) {
                                MyOutLinedDropDown(
                                    items = viewModel.categories,
                                    selected = uiState.category.takeIf { it.isNotEmpty() },
                                    onItemSelected = { selectedCategory ->
                                        viewModel.updateCategory(selectedCategory ?: "")
                                    },
                                    label = "Selecciona una categoría",
                                    hasError = uiState.categoryError != null,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                if (uiState.categoryError != null) {
                                    Text(
                                        text = uiState.categoryError ?: "",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo de descripción con icono
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Description,
                                contentDescription = "Descripción",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 8.dp, top = 8.dp)
                            )

                            MyOutlinedTextField(
                                value = uiState.description,
                                onValueChange = {
                                    if (it.length <= 300) viewModel.updateDescription(
                                        it
                                    )
                                },
                                label = "Descripción",
                                modifier = Modifier.fillMaxWidth(),
                                hasError = uiState.descriptionError != null,
                                errorMessage = uiState.descriptionError,
                                singleLine = false,
                                maxLines = 5
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Tipo de Cliente",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Selector cliente/no cliente con mejor diseño
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = uiState.isClient,
                                    onClick = { viewModel.updateIsClient(true) }
                                )
                                Text(
                                    text = stringResource(R.string.es_cliente),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp, end = 24.dp)
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                RadioButton(
                                    selected = !uiState.isClient,
                                    onClick = { viewModel.updateIsClient(false) }
                                )
                                Text(
                                    text = stringResource(R.string.no_es_cliente),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Sección específica según tipo de cliente
                        if (uiState.isClient) {
                            Text(
                                text = "Datos del Cliente",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "Cliente",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )

                                // Componente para buscar clientes
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    MyAutoCompleteTextViewCompose(
                                        items = uiState.subscriptions,
                                        label = "Cliente",
                                        selectedItem = uiState.selectedSubscription,
                                        onItemSelected = { subscription ->
                                            viewModel.updateSelectedSubscription(subscription)
                                        },
                                        onSelectionCleared = {
                                            viewModel.updateSelectedSubscription(null)
                                        },
                                        onTextChanged = { text ->
                                            searchText = text
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        hasError = uiState.subscriptionError != null
                                    )
                                    
                                    if (uiState.subscriptionError != null) {
                                        Text(
                                            text = uiState.subscriptionError ?: "",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "Ubicación",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Selector de lugares si no es cliente
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = "Ubicación",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    MyOutLinedDropDown(
                                        items = uiState.places,
                                        selected = uiState.selectedPlace,
                                        onItemSelected = { place ->
                                            viewModel.updateSelectedPlace(place)
                                        },
                                        label = "Selecciona un lugar",
                                        hasError = uiState.placeError != null,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    
                                    if (uiState.placeError != null) {
                                        Text(
                                            text = uiState.placeError ?: "",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Botón de registro con estilo destacado
                        MyButton(
                            onClick = { viewModel.createTicket() },
                            text = "Registrar Ticket",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }


            // Mostrar diálogo de error si es necesario
            if (uiState.error != null) {
                MyCustomDialog(
                    onDismissRequest = { viewModel.resetError() },
                    content = {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.error),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = uiState.error ?: "",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            MyButton(
                                onClick = { viewModel.resetError() },
                                text = stringResource(R.string.ok),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                )
            }
        }
    }
} 