package com.dscorp.ispadmin.presentation.ui.features.composecomponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit,
    label: String,
    maxLength: Int = 100,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var text by remember { mutableStateOf(value) }
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        maxLines = 1,
        value = text,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        ),
        onValueChange = {
            if (it.length > maxLength) return@OutlinedTextField
            text = it
            onValueChange(it)
        },
        placeholder = {
            Text(
                text = label,
                color = Color.Gray,
            )
        }
    )
}