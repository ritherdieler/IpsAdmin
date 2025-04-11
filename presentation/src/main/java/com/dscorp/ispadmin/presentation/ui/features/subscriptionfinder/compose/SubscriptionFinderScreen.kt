package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose

import android.content.Context
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
import com.dscorp.ispadmin.presentation.ui.features.dialog.MyCustomDialog
import com.dscorp.ispadmin.presentation.ui.features.migration.Loader
import com.dscorp.ispadmin.presentation.ui.features.migration.MigrationActivity
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.SubscriptionFinderFragmentDirections
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.changeNapBox.ChangeNapBoxComp
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.CANCEL_SUBSCRIPTION
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.CHANGE_NAP_BOX
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.EDIT_PLAN_SUBSCRIPTION
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.MIGRATE_TO_FIBER
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.SEE_DETAILS
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionMenu.SHOW_PAYMENT_HISTORY
import com.example.cleanarchitecture.domain.entity.SubscriptionResume
import com.example.data2.data.apirequestmodel.MoveOnuRequest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

const val SUBSCRIPTION_ID = "subscriptionId"

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

    SubscriptionFinder(
        subscriptions = uiState.subscriptions,
        onSearch = {
            coroutinesScope.launch {
                viewModel.documentNumberFlow.emit(it)
            }
        },
        onMenuItemSelected = { menuItem, subscription ->
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
                    showCancelSubscriptionConfirmDialog = true
                }

                CHANGE_NAP_BOX -> {
                    viewModel.setSelectedSubscription(subscription)
                    showChangeNapBoxDialog = true
                }
            }
        }
    )

    when (uiState.cancelSubscriptionState) {
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
            uiState.selectedSubscription?.id?.let { viewModel.removeSubscriptionFromList(it) }
        }
    }

    if (showCancelSubscriptionConfirmDialog) {
        MyConfirmDialog(
            title = "Cancelar suscripción",
            body = {
                Text(text = "Esta seguro de cancelar la suscripción?")
            },
            onDismissRequest = {
                showCancelSubscriptionConfirmDialog = false
            }, onAccept = {
                uiState.selectedSubscription?.id?.let {
                    viewModel.cancelSubscription(it)
                }
            }
        )
    }

    if (showChangeNapBoxDialog) {
        viewModel.getNapBoxes()

        MyCustomDialog(onDismissRequest = {
            showChangeNapBoxDialog = false
        }) {

            fun showToast(context: Context, message: String) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }

            ChangeNapBoxComp(
                currentNapBox = uiState.selectedSubscription?.napBox!!,
                napBoxListState = uiState.napBoxesState,
                onChangeNapBoxClick = { napBox ->
                    uiState.selectedSubscription?.let { subscription ->
                        when {
                            napBox.id == null -> showToast(
                                context,
                                "No se ha seleccionado un NAP BOX"
                            )

                            napBox.id == subscription.napBox?.id -> showToast(
                                context,
                                "El NAP BOX seleccionado es el mismo que el actual"
                            )

                            subscription.napBox?.id == null -> showToast(
                                context,
                                "No se puede cambiar el NAP BOX de una suscripción sin NAP BOX"
                            )

                            else -> {
                                val request = MoveOnuRequest(
                                    subscriptionId = subscription.id,
                                    newNapBoxId = napBox.id!!.toInt()
                                )
                                viewModel.changeNapBox(request)
                            }
                        }
                    } ?: showToast(context, "No se ha seleccionado una suscripción")
                },
                onNapBoxChanged = {
                    showChangeNapBoxDialog = false
                    viewModel.resetNapBoxFlow()
                }
            )
        }
    }
}
