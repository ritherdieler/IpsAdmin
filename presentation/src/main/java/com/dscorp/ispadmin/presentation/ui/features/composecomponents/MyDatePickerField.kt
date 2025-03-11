package com.dscorp.ispadmin.presentation.ui.features.composecomponents

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.localToUTC
import java.text.SimpleDateFormat
import java.util.Locale

val myDateFormat = "dd-MM-yyyy HH:mm"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerField(
    modifier: Modifier = Modifier,
    label: String,
    date: String,
    onDateSelected: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    MyClickableOutlineTextField(
        modifier = modifier,
        text = date,
        label = label,
        onClick = { showDatePicker = true }
    )

    if (showDatePicker) {
        DatePickerDialog(
            colors = DatePickerDefaults.colors().copy(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let {
                            val formattedDate = SimpleDateFormat(
                                myDateFormat,
                                Locale.getDefault()
                            ).format(it.localToUTC())
                            onDateSelected(formattedDate)
                        }
                    }, content = {
                        Text("OK")
                    })
            },
            content = {
                DatePicker(
                    state = datePickerState,
                )
            }
        )
    }
}