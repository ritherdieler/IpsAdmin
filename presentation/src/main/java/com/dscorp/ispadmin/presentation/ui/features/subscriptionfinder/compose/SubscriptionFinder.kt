package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.key.Key.Companion.I
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.dscorp.ispadmin.R
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResume
import com.google.common.io.Files.append

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubscriptionFinder() {

    var selectedOption by remember { mutableStateOf(SubscriptionFilter.values()[0]) }

    var inputText by remember { mutableStateOf("") }

    Column {
        FlowRow {
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
        when (selectedOption) {
            SubscriptionFilter.BY_NAME -> NameAndLastNameFilterForm()
            SubscriptionFilter.BY_DOCUMENT -> ByDocumentForm()
            SubscriptionFilter.BY_DATE -> ByDateForm()
        }

    }


}

@Composable
fun NameAndLastNameFilterForm() {
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            label = { Text(text = "Nombre") })

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
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
            CardFooter()

        }
    }

}

@Composable
fun CardFooter() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),

        horizontalArrangement = Arrangement.Center
    ) {
        //expandIcon
        IconButton(
            onClick = { },
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                imageVector = Icons.Default.ArrowDropDown,
                tint = Color.White,
                contentDescription = "ver mas",

                )
        }
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
        val (plan, antiquity, qualification, place, ics, lastPayment, debtViewer) = createRefs()

        Text(
            modifier = Modifier.constrainAs(plan) {
                top.linkTo(parent.top, margin = 24.dp)
                start.linkTo(parent.start, margin = 24.dp)
            },
            text = createSpannableString("Plan: ", subscriptionResume.planName),
            style = MaterialTheme.typography.subtitle1
        )

        Text(
            modifier = Modifier.constrainAs(antiquity) {
                top.linkTo(plan.bottom, margin = 8.dp)
                start.linkTo(parent.start, margin = 24.dp)
            },
            text = createSpannableString("Antiguedad: ", subscriptionResume.antiquity),
            color = Color.White,
            style = MaterialTheme.typography.subtitle1
        )

        Text(
            modifier = Modifier.constrainAs(qualification) {
                top.linkTo(antiquity.bottom, margin = 8.dp)
                start.linkTo(parent.start, margin = 24.dp)
            },
            text = createSpannableString("Calificación: ", subscriptionResume.qualification),
            color = Color.White,
            style = MaterialTheme.typography.subtitle1
        )

        Text(
            modifier = Modifier.constrainAs(place) {
                top.linkTo(qualification.bottom, margin = 8.dp)
                start.linkTo(parent.start, margin = 24.dp)
            },
            text = createSpannableString("Lugar: ", subscriptionResume.placeName),
            color = Color.White,
            style = MaterialTheme.typography.subtitle1
        )

        Text(
            modifier = Modifier.constrainAs(ics) {
                top.linkTo(place.bottom, margin = 8.dp)
                start.linkTo(parent.start, margin = 24.dp)
            },
            text = createSpannableString("ICS: ", subscriptionResume.ics),
            color = Color.White,
            style = MaterialTheme.typography.subtitle1
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
            style = MaterialTheme.typography.subtitle1
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
                .background(Color(0xFFFF7B47))
        ) {
            Text(
                text = "$pendingInvoicesQuantity",
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = "$$totalDebt", color = Color.White)
    }

}

@Preview(showSystemUi = true)
@Composable
private fun SubscriptionFinderPreview() {
    MaterialTheme {
//        SubscriptionFinder()
        val subscriptionResume = SubscriptionResume(
            id = 1,
            planName = "Fibra1",
            customerName = "Juan Perez",
            antiquity = "1 año",
            qualification = "5 estrellas",
            placeName = "Casa",
            ics = "123456",
            lastPaymentDate = "12/12/2020",
            pendingInvoicesQuantity = 2,
            totalDebt = 100.0,
        )
        SubscriptionCard(subscriptionResume)
    }

}


enum class SubscriptionFilter(val valueName: String) {
    BY_NAME("Nombre"),
    BY_DOCUMENT("Documento"),
    BY_DATE("Fecha"),
}


