package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.cleanarchitecture.domain.entity.ServiceStatus
import com.example.cleanarchitecture.domain.entity.SubscriptionResume
import com.example.cleanarchitecture.domain.entity.createReminderMessage


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubscriptionList(
    subscriptions: Map<ServiceStatus, List<SubscriptionResume>>,
    scrollState: LazyListState,
    onMenuItemSelected: (menuItem: SubscriptionMenu, subscriptionResponse: SubscriptionResume) -> Unit = { _, _ -> }
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
                SubscriptionCard(
                    backgroundColor = if (status == ServiceStatus.CANCELLED) Color(0x80140B4C)
                    else Color(0xFF140B4C),
                    subscriptionResume = subscription,
                    onMenuItemSelected = { onMenuItemSelected(it, subscription) })
            }
        }
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
        Text(text = "$$totalDebt", color = Color.White, style = MaterialTheme.typography.titleSmall)
    }

}


