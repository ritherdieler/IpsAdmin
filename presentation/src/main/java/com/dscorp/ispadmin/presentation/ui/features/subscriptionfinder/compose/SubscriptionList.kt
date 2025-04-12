package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cleanarchitecture.domain.entity.PlaceResponse
import com.example.cleanarchitecture.domain.entity.ServiceStatus
import com.example.cleanarchitecture.domain.entity.SubscriptionResume
import com.example.cleanarchitecture.domain.entity.createReminderMessage
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.CustomerFormData
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.PlacesState
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SaveSubscriptionState


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubscriptionList(
    subscriptions: Map<ServiceStatus, List<SubscriptionResume>>,
    scrollState: LazyListState,
    onMenuItemSelected: (menuItem: SubscriptionMenu, subscriptionResponse: SubscriptionResume) -> Unit = { _, _ -> },
    onSubscriptionExpanded: (SubscriptionResume, Boolean) -> Unit = { _, _ -> },
    expandedSubscriptionId: Int? = null,
    customerFormData: CustomerFormData? = null,
    placesState: PlacesState = PlacesState(),
    saveState: SaveSubscriptionState = SaveSubscriptionState.Success,
    onFieldChange: (String, String) -> Unit = { _, _ -> },
    onPlaceSelected: (PlaceResponse) -> Unit = {},
    onUpdatePlaceId: (Int, String) -> Unit = { _, _ -> },
    onSaveCustomer: () -> Unit = {}
) {
    LazyColumn(modifier = Modifier.fillMaxWidth(), state = scrollState) {
        subscriptions.forEach { (status, subscriptionList) ->
            stickyHeader {
                Text(
                    text = status.getFormattedStatus(),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
            items(items = subscriptionList, key = { it.id }) { subscription ->
                val isExpanded = expandedSubscriptionId == subscription.id
                
                SubscriptionCard(
                    subscriptionResume = subscription,
                    onMenuItemSelected = { onMenuItemSelected(it, subscription) },
                    onExpandChange = onSubscriptionExpanded,
                    expanded = isExpanded,
                    customerFormData = customerFormData,
                    placesState = placesState,
                    saveState = saveState,
                    onFieldChange = onFieldChange,
                    onPlaceSelected = onPlaceSelected,
                    onUpdatePlace = onUpdatePlaceId,
                    onSaveClick = onSaveCustomer
                )
            }
        }
    }
}

fun sendWhatsapp(subscriptionResume: SubscriptionResume, context: Context) {
    val message = when {
        subscriptionResume.totalDebt == 0.0 -> "Hola ${subscriptionResume.customerName}! 🌟\n"
        else -> subscriptionResume.createReminderMessage()
    }
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data =
            Uri.parse("http://api.whatsapp.com/send?phone=51${subscriptionResume.customer.phone}&text=$message")
    }
    context.startActivity(intent)
}

fun openInBrowser(ipAddress: String, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://$ipAddress"))
    context.startActivity(intent)
}

fun openMap(subscriptionResume: SubscriptionResume, context: Context) {
    // Get the customer address for the map query
    val address = subscriptionResume.customer.address
    val place = subscriptionResume.placeName
    
    // Combine address and place for a more accurate location
    val locationQuery = "$address, $place, Peru"
    
    try {
        // Create an intent to view the location in Google Maps
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(locationQuery)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps") // Specify Google Maps app
        
        // If Google Maps is installed, open it; otherwise, open in browser
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Fallback to opening in browser
            val mapUrl = "https://maps.google.com/?q=${Uri.encode(locationQuery)}"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))
            context.startActivity(browserIntent)
        }
    } catch (e: Exception) {
        // If there's an error, show a toast message
        Toast.makeText(context, "No se pudo abrir el mapa: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}



