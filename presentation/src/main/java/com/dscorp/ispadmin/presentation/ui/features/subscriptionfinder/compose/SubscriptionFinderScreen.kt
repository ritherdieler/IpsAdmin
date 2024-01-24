package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.dscorp.ispadmin.presentation.ui.features.dialog.MyConfirmDialog
import com.dscorp.ispadmin.presentation.ui.features.migration.Loader
import com.dscorp.ispadmin.presentation.ui.features.migration.MigrationActivity
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.SubscriptionFinderFragmentDirections
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

const val SUBSCRIPTION_ID = "subscriptionId"

@Composable
fun SubscriptionFinderScreen(
    navController: NavController,
    viewModel: SubscriptionFinderViewModel = koinViewModel(),
    ) {

    val subscriptionsFlow by viewModel.subscriptionsFlow.collectAsState()
    val coroutinesScope = rememberCoroutineScope()
    var showCancelSubscriptionConfirmDialog by remember { mutableStateOf(false) }
    val cancelSubscriptionUiState by viewModel.cancelSubscriptionFlow.collectAsState()
    val context = LocalContext.current
    var selectedSubscriptionId by remember {
        mutableStateOf<Int?>(null)
    }

    SubscriptionFinderScreen(
        subscriptions = subscriptionsFlow,
        onSearch = {
            coroutinesScope.launch {
                viewModel.documentNumberFlow.emit(it)
            }
        },
        onMenuItemSelected = { menuItem, subscriptionId ->
            when (menuItem) {
                SubscriptionMenu.SHOW_PAYMENT_HISTORY -> {
                    navController.navigate(
                        SubscriptionFinderFragmentDirections.findSubscriptionToPaymentHistoryFragment(
                            subscriptionId
                        )
                    )
                }

                SubscriptionMenu.EDIT_PLAN_SUBSCRIPTION -> {
                    navController.navigate(
                        SubscriptionFinderFragmentDirections.findSubscriptionToEditSubscriptionFragment(
                            subscriptionId
                        )
                    )
                }

                SubscriptionMenu.SEE_DETAILS -> {
                    navController.navigate(
                        SubscriptionFinderFragmentDirections.findSubscriptionToSubscriptionDetail(
                            subscriptionId
                        )
                    )
                }

                SubscriptionMenu.MIGRATE_TO_FIBER -> {
                    val intent = Intent(context, MigrationActivity::class.java)
                    intent.putExtra(SUBSCRIPTION_ID, subscriptionId)
                    context.startActivity(intent)
                }

                SubscriptionMenu.CANCEL_SUBSCRIPTION -> {
                    selectedSubscriptionId = subscriptionId
                    showCancelSubscriptionConfirmDialog = true
                }
            }
        }
    )

    when (cancelSubscriptionUiState) {
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
            viewModel.removeSubscriptionFromList(selectedSubscriptionId ?: 0)

        }
    }


    if (showCancelSubscriptionConfirmDialog)
        MyConfirmDialog(
            title = "Cancelar suscripción",
            body = {
                Text(text = "Esta seguro de cancelar la suscripción?")
            },
            onDismissRequest = {
                showCancelSubscriptionConfirmDialog = false
            }, onAccept = {


                selectedSubscriptionId?.let {
                    viewModel.cancelSubscription(it)
                }
            }
        )

}
