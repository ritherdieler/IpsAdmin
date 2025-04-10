
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RegisterSubscriptionFormScreen(
    registerSubscriptionViewModel: RegisterSubscriptionComposeViewModel = getViewModel(),
    context: Context = LocalContext.current
) {
    val locationPermissionState =
        rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    val uiState by registerSubscriptionViewModel.uiState.collectAsStateWithLifecycle()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
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
//                                registerSubscriptionViewModel.getPlaceFromCurrentLocation(
//                                    it.latitude,
//                                    it.longitude
//                                )
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
        locationPermissionState.status.isGranted -> {
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
                    registerSubscriptionViewModel.onPlaceSelected(it) },
                onNapBoxSelected = { registerSubscriptionViewModel.onNapBoxSelected(it) },
                onPLaceSelectionCleared = { registerSubscriptionViewModel.onPlaceSelectionCleared() },
                onNapBoxSelectionCleared = { registerSubscriptionViewModel.onNapBoxSelectionCleared() },
                onInstallationTypeSelected = {
                    registerSubscriptionViewModel .onInstallationTypeSelected(it)
                },
                onRegisterClick = {
                    registerSubscriptionViewModel.saveSubscription()
                }
            )
        }

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
                        Text("Request permission")
                    }
                }
            } else {
                LaunchedEffect(Unit) {
                    locationPermissionState.launchPermissionRequest()
                }
                Text("Por favor conceda el permiso o vaya a la configuración y habilite manualmente el permiso de ubicación para usar esta función.")
            }
        }
    }
}

