package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dscorp.ispadmin.R


enum class SubscriptionMenu(val menuId: Int) {
    SHOW_PAYMENT_HISTORY(R.string.show_payment_history),
    EDIT_PLAN_SUBSCRIPTION(R.string.edit_plan),
    SEE_DETAILS(R.string.see_details),
    MIGRATE_TO_FIBER(R.string.migrate_to_fiber),
    CANCEL_SUBSCRIPTION(R.string.cancel_subscription);

    fun getTitle(context: Context): String {
        return context.getString(menuId)
    }
}

@Composable
fun CardHeader(
    title: String,
    onMenuItemSelected: (SubscriptionMenu) -> Unit = {}
) {

    var dotMenuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(top = 12.dp, start = 48.dp, end = 48.dp),
            textAlign = TextAlign.Center,
            text = title.uppercase(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.White,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
        Column(modifier = Modifier.align(Alignment.CenterEnd)) {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(end = 16.dp)
                    .clickable { dotMenuExpanded = true },
                painter = painterResource(id = R.drawable.ic_more_dot),
                tint = Color.White,
                contentDescription = ""
            )

            DropdownMenu(
                expanded = dotMenuExpanded,
                onDismissRequest = { dotMenuExpanded = false }
            ) {

                SubscriptionMenu.values().forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Row {
                                Text(
                                    text = item.getTitle(context),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 17.92.sp,
                                        fontWeight = FontWeight(400),
                                        color = Color(0xFF000034),
                                    )
                                )
                            }
                        },
                        onClick = {
                            dotMenuExpanded = false
                            onMenuItemSelected(item)
                        },
                    )
                }
            }
        }
    }
}