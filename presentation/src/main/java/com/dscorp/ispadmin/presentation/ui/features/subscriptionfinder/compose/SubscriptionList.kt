package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.CustomOutlinedTextField
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResume
import com.example.cleanarchitecture.domain.domain.entity.createReminderMessage
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubscriptionList(
    subscriptions: List<SubscriptionResume>,
    scrollState: LazyListState,
    onMenuItemSelected: (menuItem: SubscriptionMenu, subscriptionId: Int) -> Unit = { _, _ -> }
) {
    LazyColumn(modifier = Modifier.fillMaxWidth(), state = scrollState) {

        stickyHeader {
            Text(
                text = "Your Header",
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .background(MaterialTheme.colors.surface)
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }

        items(items = subscriptions, key = {it.id}){ subscription->
            SubscriptionCard(
                subscriptionResume = subscription,
                onMenuItemSelected = { onMenuItemSelected(it, subscription.id) })
        }

    }
}

@Composable
fun SubscriptionCard(
    subscriptionResume: SubscriptionResume,
    onMenuItemSelected: (SubscriptionMenu) -> Unit = {}
) {
    var visible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(15.dp),
        backgroundColor = Color(0xFF140B4C)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CardHeader(
                title = subscriptionResume.customerName,
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
                backgroundColor = Color(0xFFFF9100),
                disabledBackgroundColor = Color(0xFFFF9100)
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


fun createSpannableString(fullText: String, subText: String): AnnotatedString {
    return buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color(0xFFFF9100))) {
            append(fullText)
        }
        val textDecoration =
            if (subText.isAnIp(subText)) TextDecoration.Underline else TextDecoration.None
        withStyle(style = SpanStyle(color = Color.White, textDecoration = textDecoration)) {
            append(subText)
        }
    }
}

fun String.isAnIp(subText: String): Boolean {
    return subText.matches(Regex("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$"))
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
            style = MaterialTheme.typography.subtitle2
        )

        Text(
            modifier = Modifier.constrainAs(antiquity) {
                top.linkTo(plan.bottom, margin = 8.dp)
                start.linkTo(parent.start, margin = 24.dp)
            },
            text = createSpannableString("Antiguedad: ", "${subscriptionResume.antiquity} meses"),
            color = Color.White,
            style = MaterialTheme.typography.subtitle2
        )

        Text(
            modifier = Modifier.constrainAs(qualification) {
                top.linkTo(antiquity.bottom, margin = 8.dp)
                start.linkTo(parent.start, margin = 24.dp)
            },
            text = createSpannableString("Calificación: ", subscriptionResume.qualification),
            color = Color.White,
            style = MaterialTheme.typography.subtitle2
        )

        Text(
            modifier = Modifier.constrainAs(place) {
                top.linkTo(qualification.bottom, margin = 8.dp)
                start.linkTo(parent.start, margin = 24.dp)
            },
            text = createSpannableString("Lugar: ", subscriptionResume.placeName),
            color = Color.White,
            style = MaterialTheme.typography.subtitle2
        )

        Text(
            modifier = Modifier.constrainAs(ics) {
                top.linkTo(ip.bottom, margin = 8.dp)
                start.linkTo(parent.start, margin = 24.dp)
            },
            text = createSpannableString("ICS: ", subscriptionResume.ics),
            color = Color.White,
            style = MaterialTheme.typography.subtitle2
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
            style = MaterialTheme.typography.subtitle2
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
            style = MaterialTheme.typography.subtitle2
        )

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

@Composable
fun DebtViewer(modifier: Modifier = Modifier, pendingInvoicesQuantity: Int, totalDebt: Double) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF9100))
        ) {
            Text(
                text = "$pendingInvoicesQuantity",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = "$$totalDebt", color = Color.White, style = MaterialTheme.typography.subtitle1)
    }

}


