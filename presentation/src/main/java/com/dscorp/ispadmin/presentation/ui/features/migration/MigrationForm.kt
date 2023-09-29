package com.dscorp.ispadmin.presentation.ui.features.migration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dscorp.ispadmin.R
import com.example.cleanarchitecture.domain.domain.entity.Onu
import com.example.cleanarchitecture.domain.domain.entity.Plan
import com.example.cleanarchitecture.domain.domain.entity.PlanResponse
import com.example.data2.data.apirequestmodel.MigrationRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MigrationForm(
    onus: List<Onu>,
    plans: List<PlanResponse>,
    onMigrationRequest: (MigrationRequest) -> Unit
) {
    var planDropDownExpanded by remember { mutableStateOf(false) }
    var onuDropDownExpanded by remember { mutableStateOf(false) }

    var selectedPlan by remember { mutableStateOf<PlanResponse?>(null) }
    var selectedOnu by remember { mutableStateOf<Onu?>(null) }


    var price by remember { mutableStateOf("") }

    var note by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Migración a fibra óptica")
        Spacer(modifier = Modifier.size(16.dp))

        ExposedDropdownMenuBox(
            expanded = planDropDownExpanded,
            onExpandedChange = { planDropDownExpanded = !planDropDownExpanded },
        ) {
            OutlinedTextField(
                // The `menuAnchor` modifier must be passed to the text field for correctness.
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                value = selectedPlan?.name ?: "",
                onValueChange = {},
                label = { Text("Seleccione el plan de migración") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = planDropDownExpanded) },
            )
            ExposedDropdownMenu(
                expanded = planDropDownExpanded,
                onDismissRequest = { planDropDownExpanded = false }) {
                plans.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option.name ?: "") },
                        onClick = {
                            selectedPlan = option
                            planDropDownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))
        ExposedDropdownMenuBox(
            expanded = onuDropDownExpanded,
            onExpandedChange = { onuDropDownExpanded = !onuDropDownExpanded },
        ) {
            OutlinedTextField(
                // The `menuAnchor` modifier must be passed to the text field for correctness.
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                value = selectedOnu?.sn ?: "",
                onValueChange = {},
                label = { Text("Seleccione Onu") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = onuDropDownExpanded) },
            )
            ExposedDropdownMenu(
                expanded = onuDropDownExpanded,
                onDismissRequest = { onuDropDownExpanded = false }) {
                onus.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option.sn) },
                        onClick = {
                            selectedOnu = option
                            onuDropDownExpanded = false
                        }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.size(16.dp))
        OutlinedTextField(
            value = note,
            onValueChange = {
                if (it.length < 100)
                    note = it
            },
            label = { Text(text = "Nota") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.size(16.dp))
        OutlinedTextField(
            value = price,

            onValueChange = {
                if (it.length < 5) {
                    price =
                        it.filter { it != '-' && it != ' ' && it != '.' && it != ',' && it != '\n' }
                }
            },
            label = { Text(text = "Precio") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        Spacer(modifier = Modifier.size(16.dp))
        Button(
            enabled = selectedOnu != null && selectedPlan != null,
            onClick = {
                val migrationRequest = MigrationRequest(
                    onu = selectedOnu,
                    planId = selectedPlan?.id,
                    subscriptionId = null,
                    price = price,
                    notes = note,
                )

                onMigrationRequest(migrationRequest)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Realizar migración")
        }
    }
}

@Composable
fun ErrorDialog(error: String, actionText: String = "Aceptar", onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = {

        },
        title = {
            Text(text = stringResource(R.string.error))
        },
        text = {
            Text(text = error)
        },
        confirmButton = {
            Text(modifier = Modifier.clickable {
                onDismissRequest.invoke()
            }, text = actionText)
        }
    )

}

@Composable
fun Loader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = modifier
                .width(48.dp)
                .height(48.dp),
            strokeWidth = 4.dp
        )
        Text(
            text = stringResource(R.string.loading),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MigrationFormPreview() {

    MaterialTheme {
        MigrationForm(onus = listOf(), plans = listOf(), onMigrationRequest = {})

    }
}