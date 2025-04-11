import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose.RegisterSubscriptionComposeViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose.RegisterSubscriptionForm
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RegisterSubscriptionFormScreen(
    registerSubscriptionViewModel: RegisterSubscriptionComposeViewModel = getViewModel(),
    context: Context = LocalContext.current,
    onSubscriptionRegisterSuccess: () -> Unit = {},
) {
    val locationPermissionState =
        rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    val uiState by registerSubscriptionViewModel.uiState.collectAsStateWithLifecycle()

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Estado para controlar el diálogo de GPS
    var showGpsDialog by remember { mutableStateOf(false) }

    // Estado para verificar si el GPS está habilitado
    var isGpsEnabled by remember { mutableStateOf(isGpsEnabled(context)) }

    // Launcher para abrir la configuración de ubicación
    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // Verificar nuevamente si el GPS está habilitado después de regresar de la configuración
        isGpsEnabled = isGpsEnabled(context)
    }

    when {
        uiState.registeredSubscription != null -> {
            AlertDialog(
                onDismissRequest = { registerSubscriptionViewModel.clearRegisteredSubscription() },
                title = { Text("Registro Exitoso") },
                text = {
                    Column {
                        Text("La suscripción se ha registrado correctamente:")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cliente: ${uiState.registeredSubscription?.firstName} ${uiState.registeredSubscription?.lastName}")
                        Text("DNI: ${uiState.registeredSubscription?.dni}")
                        Text("Teléfono: ${uiState.registeredSubscription?.phone}")
                        Text("Dirección: ${uiState.registeredSubscription?.address}")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "IP: ${uiState.registeredSubscription?.ip ?: "No asignada"}",
                            fontWeight = FontWeight.Bold
                        )
                        Text("Tipo de instalación: ${uiState.registeredSubscription?.installationType?.name ?: "No especificado"}")
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        registerSubscriptionViewModel.clearRegisteredSubscription()
                        onSubscriptionRegisterSuccess()
                    }) {
                        Text("Aceptar")
                    }
                }
            )
        }

        uiState.error != null -> {
            AlertDialog(
                onDismissRequest = { registerSubscriptionViewModel.clearError() },
                title = { Text("Error") },
                text = {
                    Text(
                        uiState.error ?: "Ha ocurrido un error al registrar la suscripción"
                    )
                },
                confirmButton = {
                    Button(onClick = { registerSubscriptionViewModel.clearError() }) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }

    // Verificar GPS al inicio
    LaunchedEffect(Unit) {
        isGpsEnabled = isGpsEnabled(context)
        if (!isGpsEnabled) {
            showGpsDialog = true
        }
    }

    // Diálogo para solicitar activar el GPS
    if (showGpsDialog) {
        AlertDialog(
            onDismissRequest = { showGpsDialog = false },
            title = { Text("GPS desactivado") },
            text = { Text("Para utilizar esta funcionalidad, es necesario activar el GPS de su dispositivo.") },
            confirmButton = {
                Button(
                    onClick = {
                        showGpsDialog = false
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        locationSettingsLauncher.launch(intent)
                    }
                ) {
                    Text("Activar GPS")
                }
            },
            dismissButton = {
                Button(onClick = { showGpsDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    LaunchedEffect(isGpsEnabled, locationPermissionState.status.isGranted) {
        if (isGpsEnabled && locationPermissionState.status.isGranted) {
            try {
                val currentLocationRequest = CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .build()

                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient.getCurrentLocation(currentLocationRequest, null)
                        .addOnSuccessListener { location ->
                            location?.let {
                                registerSubscriptionViewModel.onLocationChanged(
                                    LatLng(
                                        it.latitude,
                                        it.longitude
                                    )
                                )
                                registerSubscriptionViewModel.getPlaceFromCurrentLocation(
                                    it.latitude,
                                    it.longitude
                                )
                                
                                // Obtener cajas Nap cercanas a la ubicación actual
                                registerSubscriptionViewModel.getNearbyNapBoxes(
                                    it.latitude,
                                    it.longitude
                                )
                            }
                        }
                }
            } catch (e: Exception) {
            }
        }
    }

    LaunchedEffect(Unit) {
        registerSubscriptionViewModel.loadInitialFormData()
    }

    LaunchedEffect(uiState.registerSubscriptionForm.selectedPlace) {
        println(uiState.registerSubscriptionForm.selectedPlace)
    }

    when {
        // Solo mostrar el formulario si GPS está habilitado y los permisos concedidos
        isGpsEnabled && locationPermissionState.status.isGranted -> {
            RegisterSubscriptionForm(
                formState = uiState,
                onFirstNameChanged = { registerSubscriptionViewModel.onFirstNameChanged(it) },
                onLastNameChanged = { registerSubscriptionViewModel.onLastNameChanged(it) },
                onDniChanged = { registerSubscriptionViewModel.onDniChanged(it) },
                onAddressChanged = { registerSubscriptionViewModel.onAddressChanged(it) },
                onPhoneChanged = { registerSubscriptionViewModel.onPhoneChanged(it) },
                onPlanSelected = { registerSubscriptionViewModel.onPlanSelected(it) },
                onOnuSelected = { registerSubscriptionViewModel.onOnuSelected(it) },
                onPlaceSelected = {
                    registerSubscriptionViewModel.onPlaceSelected(it)
                },
                onNapBoxSelected = { registerSubscriptionViewModel.onNapBoxSelected(it) },
                onPLaceSelectionCleared = { registerSubscriptionViewModel.onPlaceSelectionCleared() },
                onNapBoxSelectionCleared = { registerSubscriptionViewModel.onNapBoxSelectionCleared() },
                onInstallationTypeSelected = {
                    registerSubscriptionViewModel.onInstallationTypeSelected(it)
                },
                onRefreshOnuList = { registerSubscriptionViewModel.refreshOnuList() },
                onRegisterClick = {
                    registerSubscriptionViewModel.saveSubscription()
                },
                onNoteChanged = { registerSubscriptionViewModel.onNoteChanged(it) },
            )
        }

        // Si el GPS está deshabilitado, mostrar mensaje para habilitarlo
        !isGpsEnabled -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Es necesario activar el GPS para continuar")
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        locationSettingsLauncher.launch(intent)
                    }
                ) {
                    Text("Activar GPS")
                }
            }
        }

        // Si el GPS está habilitado pero falta el permiso
        else -> {
            if (locationPermissionState.status.shouldShowRationale) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Se necesita permiso de ubicación para obtener su ubicación actual")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                        Text("Solicitar permiso")
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Por favor conceda el permiso o vaya a la configuración y habilite manualmente el permiso de ubicación para usar esta función.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            // Abrir la configuración de la aplicación para que el usuario active manualmente el permiso
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                            context.startActivity(intent)
                        }
                    ) {
                        Text("Ir a Configuración")
                    }
                }
            }
        }
    }
}

// Función para verificar si el GPS está habilitado
private fun isGpsEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

