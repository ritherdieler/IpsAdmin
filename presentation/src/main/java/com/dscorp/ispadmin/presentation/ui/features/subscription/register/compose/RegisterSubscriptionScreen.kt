
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose.RegisterSubscriptionComposeViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose.RegisterSubscriptionForm
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose.RegisterSubscriptionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RegisterSubscriptionFormScreen(
    registerSubscriptionViewModel: RegisterSubscriptionComposeViewModel = getViewModel()
) {
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val uiState by registerSubscriptionViewModel.myUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        registerSubscriptionViewModel.getFormData()
        locationPermissionState.launchPermissionRequest()
    }

    when {
        locationPermissionState.status.isGranted -> {
            RegisterSubscriptionForm(
                formState = RegisterSubscriptionState(
                    isLoading = false,
                    error = "numquam",
                    registeredSubscription = null,
                    registerSubscriptionForm = uiState.registerSubscriptionForm
                ),
                onFirstNameChanged = { registerSubscriptionViewModel.onFirstNameChanged(it) },
                onLastNameChanged = { registerSubscriptionViewModel.onLastNameChanged(it) },
                onDniChanged = { registerSubscriptionViewModel.onDniChanged(it) },
                onAddressChanged = { registerSubscriptionViewModel.onAddressChanged(it) },
                onPhoneChanged = { registerSubscriptionViewModel.onPhoneChanged(it) },
                onPriceChanged = { registerSubscriptionViewModel.onPriceChanged(it) },
                onPlanSelected = { registerSubscriptionViewModel.onPlanSelected(it) },
                onOnuSelected = { registerSubscriptionViewModel.onOnuSelected(it) },
            )
        }
        locationPermissionState.status.shouldShowRationale  -> {
            LaunchedEffect(Unit) {
                locationPermissionState.launchPermissionRequest()
            }
        }
        else -> {
            Text("Please go to settings and enable location permission manually to use this feature.")
        }
    }
}