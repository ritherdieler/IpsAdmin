package com.dscorp.ispadmin.presentation.ui.features.composecomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> MyOutLinedDropDown(
    modifier: Modifier = Modifier,
    items: List<T>,
    label: String? = null,
    onItemSelected: (T) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<T?>(null) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption?.toString() ?: "",
            onValueChange = { },
            label = { label?.let { Text(text = it) } },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .clickable { expanded = true }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { option ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        selectedOption = option
                        expanded = false
                        onItemSelected(option)
                    },
                    text = {
                        Text(text = option?.toString() ?: "")
                    }
                )
            }
        }
    }
}