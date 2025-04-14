package com.dscorp.ispadmin.presentation.ui.features.supportTicket.create

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import com.example.cleanarchitecture.domain.entity.PlaceResponse
import com.example.cleanarchitecture.domain.entity.SubscriptionFastSearchResponse
import com.example.data2.data.apirequestmodel.AssistanceTicketRequest
import kotlinx.coroutines.delay

@Composable
fun CreateSupportTicketScreen(
    viewModel: CreateSupportTicketViewModel,
    onTicketCreated: () -> Unit
) {
    // Recoger el estado de la UI
    val uiState by viewModel.uiState.collectAsState()
    
    var phone by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isClient by remember { mutableStateOf(true) }
    var selectedPlace by remember { mutableStateOf<PlaceResponse?>(null) }
    var selectedSubscription by remember { mutableStateOf<SubscriptionFastSearchResponse?>(null) }
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
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            // Título
            Text(
                text = stringResource(R.string.create_support_tickets),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tarjeta principal
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Campo de teléfono
                    MyOutlinedTextField(
                        value = phone,
                        onValueChange = { if (it.length <= 9) phone = it },
                        label = "Teléfono de contacto",
                        modifier = Modifier.fillMaxWidth(),
                        hasError = false,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Campo de categoría
                    MyOutLinedDropDown(
                        items = viewModel.categories,
                        selected = category.takeIf { it.isNotEmpty() },
                        onItemSelected = { selectedCategory ->
                            category = selectedCategory ?: ""
                        },
                        label = "Selecciona una categoría",
                        hasError = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Campo de descripción
                    MyOutlinedTextField(
                        value = description,
                        onValueChange = { if (it.length <= 300) description = it },
                        label = "Descripción",
                        modifier = Modifier.fillMaxWidth(),
                        hasError = false,
                        singleLine = false,
                        maxLines = 5
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Selector cliente/no cliente
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isClient,
                            onClick = { 
                                isClient = true
                                selectedPlace = null
                            }
                        )
                        Text(
                            text = stringResource(R.string.es_cliente),
                            modifier = Modifier.padding(start = 8.dp, end = 24.dp)
                        )
                        
                        Spacer(modifier = Modifier.padding(horizontal = 16.dp))
                        
                        RadioButton(
                            selected = !isClient,
                            onClick = { 
                                isClient = false
                                selectedSubscription = null
                            }
                        )
                        Text(
                            text = stringResource(R.string.no_es_cliente),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Mostrar campo de suscriptor si es cliente
                    if (isClient) {
                        MyAutoCompleteTextViewCompose(
                            items = uiState.subscriptions,
                            label = "Cliente",
                            selectedItem = selectedSubscription,
                            onItemSelected = { subscription ->
                                selectedSubscription = subscription
                            },
                            onSelectionCleared = {
                                selectedSubscription = null
                            },
                            onTextChanged = { text ->
                                searchText = text
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // Mostrar selector de lugares si no es cliente
                        MyOutLinedDropDown(
                            items = uiState.places,
                            selected = selectedPlace,
                            onItemSelected = { place ->
                                selectedPlace = place
                            },
                            label = "Selecciona un lugar",
                            hasError = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Botón de registro
                    MyButton(
                        onClick = {
                            val ticketRequest = AssistanceTicketRequest(
                                phone = phone,
                                category = category,
                                description = description,
                                subscriptionId = selectedSubscription?.id,
                                customerName = selectedSubscription?.fullName ?: "",
                                placeName = selectedPlace?.name
                            )
                            viewModel.createTicket(ticketRequest)
                        },
                        text = "Registrar",
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