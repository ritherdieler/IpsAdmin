package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.CustomOutlinedTextField
import com.example.cleanarchitecture.domain.domain.entity.CustomerData
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResume


@Composable
fun SubscriptionFinder(subscriptions: List<SubscriptionResume>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SubscriptionFinderFilters()
        SubscriptionList(subscriptions)
    }
}

@Composable
fun SubscriptionList(subscriptions: List<SubscriptionResume>) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(subscriptions.size) { index ->
            SubscriptionCard(subscriptions[index])
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubscriptionFinderFilters() {

    var selectedOption by remember { mutableStateOf(SubscriptionFilter.values()[0]) }

    var inputText by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)) {
        FlowRow(modifier = Modifier.fillMaxWidth()) {
            SubscriptionFilter.values().forEach {
                Row(
                    modifier = Modifier.selectable(
                        selected = (it == selectedOption),
                        onClick = { selectedOption = it },
                    ), verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (it == selectedOption),
                        onClick = { selectedOption = it }
                    )
                    Text(text = it.valueName)
                }
            }
        }

        Crossfade(targetState = selectedOption, label = "") { filter ->
            when (filter) {
                SubscriptionFilter.BY_NAME -> NameAndLastNameFilterForm()
                SubscriptionFilter.BY_DOCUMENT -> ByDocumentForm()
                SubscriptionFilter.BY_DATE -> ByDateForm()
            }

        }

    }


}

@Composable
fun NameAndLastNameFilterForm(modifier: Modifier = Modifier) {

    var nameText by remember { mutableStateOf("") }
    var lastNameText by remember { mutableStateOf("") }

    Row(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = nameText,
            onValueChange = { nameText = it },
            label = { Text(text = "Nombre") })
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = lastNameText,
            onValueChange = { lastNameText = it },
            label = { Text(text = "Apellido") })
    }
}

@Composable
fun ByDocumentForm() {
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            label = { Text(text = "Apellido") })
    }

}

@Composable
fun ByDateForm() {
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            label = { Text(text = "Fecha") })
    }

}


@Composable
fun SubscriptionCard(
    subscriptionResume: SubscriptionResume
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
            CardHeader(subscriptionResume.customerName)
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
fun CustomerDataForm(subscriptionResume: SubscriptionResume) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2E2064))
    ) {
        val (name, lastName, phone, dni, address, email, place, saveButton) = createRefs()

        CustomOutlinedTextField(
            modifier = Modifier.constrainAs(name) {
                top.linkTo(parent.top, margin = 8.dp)
                start.linkTo(parent.start, margin = 24.dp)
                end.linkTo(lastName.start, margin = 8.dp)
                width = Dimension.fillToConstraints
            },
            value = subscriptionResume.customer.name,
            onValueChange = {},
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
            onValueChange = {},
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
            onValueChange = {},
            label = "Teléfono",
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
            onValueChange = {},
            label = "DNI",
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
            onValueChange = {},
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
            onValueChange = {},
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
            onValueChange = {},
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Button(modifier = Modifier.constrainAs(saveButton) {
            top.linkTo(email.bottom, margin = 8.dp)
            start.linkTo(parent.start, margin = 24.dp)
            end.linkTo(parent.end, margin = 24.dp)
            bottom.linkTo(parent.bottom, margin = 16.dp)
            width = Dimension.fillToConstraints
        },
            shape = RoundedCornerShape(15.dp),
            content = {
                Text(text = "Guardar", color = Color.White)
            },
            onClick = { /*TODO*/ })


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
        withStyle(style = SpanStyle(color = Color.White)) {
            append(subText)
        }
    }
}

@Composable
fun CardBody(subscriptionResume: SubscriptionResume) {
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (plan, antiquity, qualification, place, ics, reference, ip, lastPayment, debtViewer) = createRefs()

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
            text = createSpannableString("Antiguedad: ", subscriptionResume.antiquity),
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
            modifier = Modifier.constrainAs(ip) {
                top.linkTo(place.bottom, margin = 8.dp)
                start.linkTo(parent.start, margin = 24.dp)
            },
            text = createSpannableString("IP: ", subscriptionResume.ics),
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

        Text(
            modifier = Modifier.constrainAs(lastPayment) {
                top.linkTo(ics.top)
                end.linkTo(parent.end, margin = 24.dp)
            },
            text = createSpannableString("Ultimo pago: ", subscriptionResume.lastPaymentDate),
            color = Color.White,
            style = MaterialTheme.typography.subtitle2
        )

    }
}


@Composable
fun DebtViewer(modifier: Modifier = Modifier, pendingInvoicesQuantity: Int, totalDebt: Double) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .size(40.dp)
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

@Preview(showSystemUi = true)
@Composable
private fun SubscriptionFinderPreview() {
    MaterialTheme {
        SubscriptionFinder(subscriptions = generateMockSubscriptions())
    }

}

fun generateMockSubscriptions(): List<SubscriptionResume> {
    val mockList = mutableListOf<SubscriptionResume>()
    for (i in 1..5) {
        val subscriptionResume = SubscriptionResume(
            id = i,
            planName = "Plan $i",
            customerName = "Cliente $i",
            antiquity = "${i} año(s)",
            qualification = "${i} estrella(s)",
            placeName = "Lugar $i",
            ics = "12345$i",
            lastPaymentDate = "12/12/2${i + 1}",
            pendingInvoicesQuantity = i,
            totalDebt = 100.0 * i,
            customer = CustomerData(
                name = "Nombre $i",
                lastName = "Apellido $i",
                dni = "4827183$i",
                place = "Lugar $i",
                address = "Dirección $i",
                phone = "98765432$i",
                email = "cliente$i@gmail.com"
            )
        )
        mockList.add(subscriptionResume)
    }
    return mockList
}

enum class SubscriptionFilter(val valueName: String) {
    BY_NAME("Nombre"),
    BY_DOCUMENT("Documento"),
    BY_DATE("Fecha"),
}


