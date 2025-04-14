package com.dscorp.ispadmin.presentation.ui.features.composecomponents

import MyTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MyProgressButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    debounceTimeMillis: Long = 1000
) {
    var canClick by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    Button(
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(size = 34.dp),
        onClick = {
            if (!isLoading)
                if (canClick) {
                    canClick = false
                    onClick()
                    // Reset the canClick flag after the debounce time using a custom coroutine scope
                    coroutineScope.launch {
                        delay(debounceTimeMillis)
                        canClick = true
                    }
                }
        },
    ) {
        Column {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(17.5.dp),
                    color = Color(0xFF0098F8),
                    trackColor = Color(0xFFFFFFFF),
                    strokeWidth = 3.dp
                )
            } else {
                Text(
                    text = text,
                )
            }
        }
    }

}

@Composable
@Preview(showBackground = true)
fun PreviewMyProgressButton() {
    MyTheme {
        MyProgressButton(
            text = "Iniciar sesión",
            enabled = true,
            isLoading = false,
            onClick = {}
        )
    }
}