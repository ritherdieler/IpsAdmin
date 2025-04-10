package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.CustomOutlinedTextField
import com.example.cleanarchitecture.domain.entity.SubscriptionResume
import org.koin.androidx.compose.koinViewModel

@Composable
fun SubscriptionCard(
    backgroundColor: Color = Color(0xFF140B4C),
    subscriptionResume: SubscriptionResume,
    onMenuItemSelected: (SubscriptionMenu) -> Unit = {}
) {
    var visible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = backgroundColor
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CardHeader(
                subscription = subscriptionResume,
                onMenuItemSelected = onMenuItemSelected
            )
            CardBody(subscriptionResume)
            CardFooter(
                icon = if (visible) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                onClick = { visible = !visible }
            )
            AnimatedVisibility(visible = visible) {
                CustomerDataForm(subscriptionResume)
            }
        }
    }

}

@Composable
fun CustomerDataForm(
    subscriptionResume: SubscriptionResume,
    viewModel: SubscriptionFinderViewModel = koinViewModel()
) {

    val updateCustomerState by viewModel.updateCustomerDataFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2E2064))
    ) {

        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {

            val (name, lastName, phone, dni, address, email, place) = createRefs()

            CustomOutlinedTextField(
                modifier = Modifier.constrainAs(name) {
                    top.linkTo(parent.top, margin = 8.dp)
                    start.linkTo(parent.start, margin = 24.dp)
                    end.linkTo(lastName.start, margin = 8.dp)
                    width = Dimension.fillToConstraints
                },
                value = subscriptionResume.customer.name,
                onValueChange = {
                    subscriptionResume.customer.name = it
                },
                label = "Nombre"
            )

            CustomOutlinedTextField(
                modifier = Modifier.constrainAs(lastName) {
                    top.linkTo(name.top)
                    start.linkTo(name.end, margin = 8.dp)
                    end.linkTo(parent.end, margin = 24.dp)
                    width = Dimension.fillToConstraints
                },
                value = subscriptionResume.customer.lastName,
                onValueChange = {
                    subscriptionResume.customer.lastName = it
                },
                label = "Apellido"
            )

            CustomOutlinedTextField(
                modifier = Modifier.constrainAs(phone) {
                    top.linkTo(name.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 24.dp)
                    end.linkTo(dni.start, margin = 8.dp)
                    width = Dimension.fillToConstraints
                },
                value = subscriptionResume.customer.phone,
                onValueChange = {
                    subscriptionResume.customer.phone = it
                },
                label = "Teléfono",
                maxLength = 9,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            CustomOutlinedTextField(
                modifier = Modifier.constrainAs(dni) {
                    top.linkTo(phone.top)
                    start.linkTo(phone.end, margin = 8.dp)
                    end.linkTo(parent.end, margin = 24.dp)
                    width = Dimension.fillToConstraints
                },
                value = subscriptionResume.customer.dni,
                onValueChange = {
                    subscriptionResume.customer.dni = it
                },
                label = "DNI",
                maxLength = 8,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            CustomOutlinedTextField(
                modifier = Modifier.constrainAs(place) {
                    top.linkTo(dni.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 24.dp)
                    end.linkTo(parent.end, margin = 24.dp)
                    width = Dimension.fillToConstraints
                },
                value = subscriptionResume.customer.place,
                onValueChange = {
                    subscriptionResume.customer.place = it
                },
                label = "Lugar"
            )

            CustomOutlinedTextField(
                modifier = Modifier.constrainAs(address) {
                    top.linkTo(place.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 24.dp)
                    end.linkTo(parent.end, margin = 24.dp)
                    width = Dimension.fillToConstraints
                },
                value = subscriptionResume.customer.address,
                onValueChange = {
                    subscriptionResume.customer.address = it
                },
                label = "Dirección"
            )

            CustomOutlinedTextField(
                modifier = Modifier.constrainAs(email) {
                    top.linkTo(address.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 24.dp)
                    end.linkTo(parent.end, margin = 24.dp)
                    width = Dimension.fillToConstraints
                },
                value = subscriptionResume.customer.email,
                onValueChange = {
                    subscriptionResume.customer.email = it
                },
                label = "Email",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
        }


        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(start = 24.dp, end = 24.dp, bottom = 16.dp, top = 8.dp),
            shape = RoundedCornerShape(15.dp),
            content = {
                Row {
                    if (updateCustomerState is SaveSubscriptionState.Loading)
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        ) else
                        Text(text = "Guardar", color = Color.White)
                }
            },
            enabled = updateCustomerState !is SaveSubscriptionState.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9100),
               disabledContentColor = Color(0xFFFF9100)
            ),
            onClick = {
                viewModel.updateCustomerData(subscriptionResume.customer)
            }
        )
    }
}

@Composable
fun CardFooter(icon: ImageVector, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(40.dp),
            imageVector = icon,
            tint = Color.White,
            contentDescription = "ver mas",
        )
    }
}

@Composable
fun CardBody(subscriptionResume: SubscriptionResume) {
    val context = LocalContext.current

    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (plan, antiquity, qualification, place, ics, ip, lastPayment, debtViewer, whatsappButton) = createRefs()

        Text(
            modifier = Modifier.constrainAs(plan) {
                top.linkTo(parent.top, margin = 8.dp)
                start.linkTo(parent.start, margin = 24.dp)
            },
            text = createSpannableString("Plan: ", subscriptionResume.planName),
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            modifier = Modifier.constrainAs(antiquity) {
                top.linkTo(plan.bottom, margin = 8.dp)
                start.linkTo(parent.start, margin = 24.dp)
            },
            text = createSpannableString("Antiguedad: ", "${subscriptionResume.antiquity} meses"),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            modifier = Modifier.constrainAs(qualification) {
                top.linkTo(antiquity.bottom, margin = 8.dp)
                start.linkTo(parent.start, margin = 24.dp)
            },
            text = createSpannableString("Calificación: ", subscriptionResume.qualification),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            modifier = Modifier.constrainAs(place) {
                top.linkTo(qualification.bottom, margin = 8.dp)
                start.linkTo(parent.start, margin = 24.dp)
            },
            text = createSpannableString("Lugar: ", subscriptionResume.placeName),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            modifier = Modifier.constrainAs(ics) {
                top.linkTo(ip.bottom, margin = 8.dp)
                start.linkTo(parent.start, margin = 24.dp)
            },
            text = createSpannableString("ICS: ", subscriptionResume.ics),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            modifier = Modifier
                .clickable {
                    openInBrowser(subscriptionResume.ipAddress, context)
                }
                .constrainAs(ip) {
                    top.linkTo(place.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 24.dp)
                },
            text = createSpannableString("IP: ", subscriptionResume.ipAddress),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )

        DebtViewer(
            modifier = Modifier.constrainAs(debtViewer) {
                top.linkTo(plan.top)
                end.linkTo(parent.end, margin = 24.dp)

            },
            pendingInvoicesQuantity = subscriptionResume.pendingInvoicesQuantity,
            totalDebt = subscriptionResume.totalDebt
        )

        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    sendWhatsapp(subscriptionResume, context)
                }
                .padding(8.dp)
                .size(24.dp)
                .constrainAs(whatsappButton) {
                    top.linkTo(debtViewer.bottom, margin = 8.dp)
                    start.linkTo(debtViewer.start)
                    end.linkTo(debtViewer.end)
                    bottom.linkTo(lastPayment.top, margin = 8.dp)
                },
            tint = Color(0xFF25D366),
            painter = painterResource(id = R.drawable.ic_whatsapp),
            contentDescription = "enviar mensaje por whatsapp"
        )

        Text(
            modifier = Modifier.constrainAs(lastPayment) {
                top.linkTo(ics.top)
                end.linkTo(parent.end, margin = 24.dp)
            },
            text = createSpannableString("Ultimo pago: ", subscriptionResume.lastPaymentDate ?: ""),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}