package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dscorp.ispadmin.R
@Composable
fun CardHeader(
    title: String,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            textAlign = TextAlign.Center,
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().align(Alignment.Center).padding(top = 12.dp)
        )
            Icon(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp),
                painter = painterResource(id = R.drawable.ic_more_dot),
                tint = Color.White,
                contentDescription = ""
            )
    }
}