package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.CustomOutlinedTextField
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyOutLinedDropDown
import com.example.cleanarchitecture.domain.entity.SubscriptionResume
import org.koin.androidx.compose.koinViewModel

/**
 * Card component displaying a subscription with expandable details.
 * 
 * @param subscriptionResume Data for the subscription to display
 * @param onMenuItemSelected Callback when a menu item is selected
 * @param modifier Optional modifier for customizing the component
 */
@Composable
fun SubscriptionCard(
    subscriptionResume: SubscriptionResume,
    onMenuItemSelected: (SubscriptionMenu) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    // Calculate primary background color from Material3 theme
    val cardBackgroundColor = MaterialTheme.colorScheme.primaryContainer
    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CardHeader(
                subscription = subscriptionResume,
                onMenuItemSelected = onMenuItemSelected
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            CardBody(subscriptionResume = subscriptionResume)

            ExpandableCardFooter(
                expanded = expanded,
                onClick = { expanded = !expanded }
            )

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                CustomerDataForm(subscriptionResume = subscriptionResume)
            }
        }
    }
}

/**
 * Footer component with animation for expanding/collapsing card details
 */
@Composable
fun ExpandableCardFooter(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationDegree by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotation"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = if (expanded) "Collapse" else "Expand",
            modifier = Modifier
                .size(32.dp)
                .rotate(rotationDegree),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * Form for editing customer data inside a subscription card
 */
@Composable
fun CustomerDataForm(
    subscriptionResume: SubscriptionResume,
    viewModel: SubscriptionFinderViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        val currentPlace = uiState.placesState.places.find {
            it.id == subscriptionResume.placeId
        }
        currentPlace?.let { viewModel.onPlaceSelected(it) }
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        tonalElevation = 2.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            CustomerFormFields(
                subscriptionResume = subscriptionResume,
                placesState = uiState.placesState,
                onPlaceSelected = viewModel::onPlaceSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            SaveButton(
                saveState = uiState.saveSubscriptionState,
                onSaveClick = { viewModel.updateCustomerData(subscriptionResume.customer) }
            )
        }
    }
}

@Composable
private fun CustomerFormFields(
    subscriptionResume: SubscriptionResume,
    placesState: PlacesState,
    onPlaceSelected: (com.example.cleanarchitecture.domain.entity.PlaceResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier.fillMaxWidth()) {
        val (name, lastName, phone, dni, address, email, place) = createRefs()

        // Name and Last Name row
        CustomOutlinedTextField(
            modifier = Modifier.constrainAs(name) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(lastName.start, margin = 8.dp)
                width = Dimension.fillToConstraints
            },
            value = subscriptionResume.customer.name,
            onValueChange = { subscriptionResume.customer.name = it },
            label = "Nombre"
        )

        CustomOutlinedTextField(
            modifier = Modifier.constrainAs(lastName) {
                top.linkTo(name.top)
                start.linkTo(name.end, margin = 8.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            value = subscriptionResume.customer.lastName,
            onValueChange = { subscriptionResume.customer.lastName = it },
            label = "Apellido"
        )

        // Phone and DNI row
        CustomOutlinedTextField(
            modifier = Modifier.constrainAs(phone) {
                top.linkTo(name.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(dni.start, margin = 8.dp)
                width = Dimension.fillToConstraints
            },
            value = subscriptionResume.customer.phone,
            onValueChange = { subscriptionResume.customer.phone = it },
            label = "Teléfono",
            maxLength = 9,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        CustomOutlinedTextField(
            modifier = Modifier.constrainAs(dni) {
                top.linkTo(phone.top)
                start.linkTo(phone.end, margin = 8.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            value = subscriptionResume.customer.dni,
            onValueChange = { subscriptionResume.customer.dni = it },
            label = "DNI",
            maxLength = 8,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Place dropdown with loading states
        PlaceSelector(
            modifier = Modifier.constrainAs(place) {
                top.linkTo(dni.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            placesState = placesState,
            subscriptionResume = subscriptionResume,
            onPlaceSelected = onPlaceSelected
        )

        // Address and Email fields
        CustomOutlinedTextField(
            modifier = Modifier.constrainAs(address) {
                top.linkTo(place.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            value = subscriptionResume.customer.address,
            onValueChange = { subscriptionResume.customer.address = it },
            label = "Dirección"
        )

        CustomOutlinedTextField(
            modifier = Modifier.constrainAs(email) {
                top.linkTo(address.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            value = subscriptionResume.customer.email,
            onValueChange = { subscriptionResume.customer.email = it },
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
    }
}

@Composable
private fun PlaceSelector(
    placesState: PlacesState,
    subscriptionResume: SubscriptionResume,
    onPlaceSelected: (com.example.cleanarchitecture.domain.entity.PlaceResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        placesState.places.isNotEmpty() -> {
            MyOutLinedDropDown(
                modifier = modifier,
                items = placesState.places,
                selected = placesState.selectedPlace,
                label = "Lugar",
                onItemSelected = { selectedPlace ->
                    subscriptionResume.customer.place = selectedPlace.name!!
                    subscriptionResume.customer.placeId = selectedPlace.id!!.toInt()
                    onPlaceSelected(selectedPlace)
                }
            )
        }
        placesState.isLoading -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        placesState.error != null -> {
            CustomOutlinedTextField(
                modifier = modifier,
                enabled = false,
                value = subscriptionResume.customer.place,
                onValueChange = { subscriptionResume.customer.place = it },
                label = "Lugar"
            )
        }
    }
}

@Composable
private fun SaveButton(
    saveState: SaveSubscriptionState,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
        ),
        enabled = saveState !is SaveSubscriptionState.Loading,
        onClick = onSaveClick
    ) {
        if (saveState is SaveSubscriptionState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "Guardar",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun CardBody(subscriptionResume: SubscriptionResume, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(0.6f)) {
                SubscriptionInfoItem(
                    label = "Plan",
                    value = subscriptionResume.planName.capitalize(),
                    alignment = Alignment.CenterStart
                )
                SubscriptionInfoItem(
                    label = "Antigüedad",
                    value = "${subscriptionResume.antiquity} meses",
                    alignment = Alignment.CenterStart
                )
                SubscriptionInfoItem(
                    label = "Calificación",
                    value = subscriptionResume.qualification,
                    alignment = Alignment.CenterStart
                )
                SubscriptionInfoItem(
                    label = "Lugar",
                    value = subscriptionResume.placeName.capitalize(),
                    alignment = Alignment.CenterStart
                )
                SubscriptionInfoItem(
                    label = "IP",
                    value = subscriptionResume.ipAddress,
                    isClickable = true,
                    onClick = { openInBrowser(subscriptionResume.ipAddress, context) },
                    alignment = Alignment.CenterStart
                )
                SubscriptionInfoItem(
                    label = "ICS",
                    value = subscriptionResume.ics,
                    alignment = Alignment.CenterStart
                )
            }
            
            Column(
                modifier = Modifier.weight(0.5f),
                horizontalAlignment = Alignment.End
            ) {
                // Debt info
                DebtViewer(
                    pendingInvoicesQuantity = subscriptionResume.pendingInvoicesQuantity,
                    totalDebt = subscriptionResume.totalDebt
                )
                
                // WhatsApp button
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clip(CircleShape)
                        .clickable { sendWhatsapp(subscriptionResume, context) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_whatsapp),
                        contentDescription = "Enviar mensaje por WhatsApp",
                        modifier = Modifier.size(28.dp),
                        tint = Color(0xFF25D366)
                    )
                }
                
                // Last payment info
                SubscriptionInfoItem(
                    label = "Últ. Pago",
                    value = subscriptionResume.lastPaymentDate ?: "Sin pagos",
                    alignment = Alignment.CenterEnd
                )
            }
        }
    }
}

@Composable
fun SubscriptionInfoItem(
    label: String,
    value: String,
    isClickable: Boolean = false,
    onClick: () -> Unit = {},
    alignment: Alignment = Alignment.Center,
    modifier: Modifier = Modifier
) {
    val baseModifier = modifier
        .padding(vertical = 4.dp)
        .fillMaxWidth()
    
    val textModifier = if (isClickable) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }
    
    Box(
        modifier = baseModifier,
        contentAlignment = alignment
    ) {
        Row(
            modifier = textModifier
        ) {
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isClickable) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
