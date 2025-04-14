package com.dscorp.ispadmin.presentation.ui.features.composecomponents

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MyOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onValueChange: (String) -> Unit,
    hasError: Boolean = false,
    singleLine: Boolean = false,
    maxLines: Int? = null,
    enabled : Boolean = true,
    readOnly: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        label = { Text(label) },
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        isError = hasError,
        supportingText = errorMessage?.let { { Text(it) } } ?: supportingText,
        singleLine = singleLine,
        maxLines = maxLines ?: 1,
        enabled = enabled,
        readOnly = readOnly
    )
}