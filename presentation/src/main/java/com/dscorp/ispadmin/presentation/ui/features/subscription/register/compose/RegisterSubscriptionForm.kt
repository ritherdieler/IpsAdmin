package com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose

import MyTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dscorp.ispadmin.presentation.composeComponents.MyAutoCompleteTextViewCompose
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyButton
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyOutLinedDropDown
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyOutlinedTextField
import com.example.cleanarchitecture.domain.domain.entity.NapBoxResponse
import com.example.cleanarchitecture.domain.domain.entity.Onu
import com.example.cleanarchitecture.domain.domain.entity.PlaceResponse
import com.example.cleanarchitecture.domain.domain.entity.PlanResponse

@Composable
fun RegisterSubscriptionForm(
    modifier: Modifier = Modifier,
    formState: RegisterSubscriptionState,
    onFirstNameChanged: (String) -> Unit = {},
    onLastNameChanged: (String) -> Unit = {},
    onDniChanged: (String) -> Unit = {},
    onAddressChanged: (String) -> Unit = {},
    onPhoneChanged: (String) -> Unit = {},
    onPlanSelected: (PlanResponse) -> Unit = {},
    onOnuSelected: (Onu) -> Unit = {},
    onPlaceSelected: (PlaceResponse) -> Unit = {},
    onNapBoxSelected: (NapBoxResponse) -> Unit = {},
    onPLaceSelectionCleared: () -> Unit = {},
    onNapBoxSelectionCleared: () -> Unit = {},
    onInstallationTypeSelected: (String) -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        val scrollState = rememberScrollState()

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            FormRow {
                MyOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    onValueChange = onFirstNameChanged,
                    value = formState.registerSubscriptionForm.firstName,
                    label = FIRST_NAME_LABEL,
                    hasError = formState.registerSubscriptionForm.firstNameError != null
                )
                MyOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    onValueChange = onLastNameChanged,
                    value = formState.registerSubscriptionForm.lastName,
                    label = LAST_NAME_LABEL,
                    hasError = formState.registerSubscriptionForm.lastNameError != null
                )
            }

            FormRow {
                MyOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    onValueChange = onDniChanged,
                    value = formState.registerSubscriptionForm.dni,
                    label = DNI_LABEL,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    hasError = formState.registerSubscriptionForm.dniError != null
                )
                MyOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    onValueChange = onPhoneChanged,
                    value = formState.registerSubscriptionForm.phone,
                    label = PHONE_LABEL,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    hasError = formState.registerSubscriptionForm.phoneError != null
                )
            }

            MyAutoCompleteTextViewCompose(
                modifier = Modifier.fillMaxWidth(),
                items = formState.registerSubscriptionForm.placeList,
                label = PLACE_LABEL,
                selectedItem = formState.registerSubscriptionForm.selectedPlace,
                onItemSelected = onPlaceSelected,
                onSelectionCleared = onPLaceSelectionCleared,
                hasError = formState.registerSubscriptionForm.placeError != null,
            )

            MyOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onAddressChanged,
                value = formState.registerSubscriptionForm.address,
                label = ADDRESS_LABEL,
                hasError = formState.registerSubscriptionForm.addressError != null
            )
            var installationType by remember { mutableStateOf(FIBER_OPTIC) }

            InstallationTypeSelector(
                installationType = installationType,
                onTypeSelected = {
                    installationType = it
                    onInstallationTypeSelected(it)
                }
            )

            MyOutLinedDropDown(
                label = PLAN_LABEL,
                items = formState.registerSubscriptionForm.planList,
                selected = formState.registerSubscriptionForm.selectedPlan,
                onItemSelected = onPlanSelected,
                hasError = formState.registerSubscriptionForm.planError != null,
                enabled = formState.registerSubscriptionForm.planList.isNotEmpty(),
            )

            if (installationType == FIBER_OPTIC) {
                FiberOpticForm(
                    formState = formState,
                    onOnuSelected = onOnuSelected,
                    onNapBoxSelected = onNapBoxSelected,
                    onNapBoxSelectionCleared = onNapBoxSelectionCleared
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            MyButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Registrar",
                onClick = onRegisterClick,
                enabled = formState.registerSubscriptionForm.isValid()
            )

        }
    }
}

@Composable
fun InstallationTypeSelector(
    installationType: String,
    onTypeSelected: (String) -> Unit
) {
    Row {
        RadioButtonWithLabel(
            modifier = Modifier.weight(1f),
            label = FIBER_OPTIC,
            selected = installationType == FIBER_OPTIC,
            onClick = { onTypeSelected(FIBER_OPTIC) }
        )
        RadioButtonWithLabel(
            modifier = Modifier.weight(1f),
            label = WIRELESS,
            selected = installationType == WIRELESS,
            onClick = { onTypeSelected(WIRELESS) }
        )
    }
}

@Composable
fun RadioButtonWithLabel(
    modifier: Modifier,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun FiberOpticForm(
    formState: RegisterSubscriptionState,
    onOnuSelected: (Onu) -> Unit,
    onNapBoxSelected: (NapBoxResponse) -> Unit,
    onNapBoxSelectionCleared: () -> Unit
) {
    MyOutLinedDropDown(
        items = formState.registerSubscriptionForm.onuList,
        selected = formState.registerSubscriptionForm.selectedOnu,
        label = ONU_LABEL,
        onItemSelected = onOnuSelected,
        hasError = formState.registerSubscriptionForm.onuError != null
    )
    MyAutoCompleteTextViewCompose(
        items = formState.registerSubscriptionForm.napBoxList,
        label = NAP_BOX_LABEL,
        selectedItem = formState.registerSubscriptionForm.selectedNapBox,
        onItemSelected = onNapBoxSelected,
        onSelectionCleared = onNapBoxSelectionCleared,
        enabled = formState.registerSubscriptionForm.selectedPlace != null,
        hasError = formState.registerSubscriptionForm.placeError != null
    )
}

@Composable
fun FormRow(
    modifier: Modifier = Modifier,
    horizontalGap: Dp = 8.dp,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(horizontalGap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

// Constantes para etiquetas
private const val FIRST_NAME_LABEL = "Nombres"
private const val LAST_NAME_LABEL = "Apellidos"
private const val DNI_LABEL = "Dni"
private const val ADDRESS_LABEL = "Direccion"
private const val PHONE_LABEL = "Telefono"
private const val PLACE_LABEL = "Lugar"
private const val PLAN_LABEL = "Plan"
private const val ONU_LABEL = "Onu"
private const val NAP_BOX_LABEL = "Caja Nap"
const val FIBER_OPTIC = "Fibra óptica"
const val WIRELESS = "Inalámbrico"

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL)
@Composable
private fun RegisterSubscriptionPreview() {
    MyTheme {
        RegisterSubscriptionForm(
            modifier = Modifier.statusBarsPadding(),
            formState = RegisterSubscriptionState(
                isLoading = false,
                error = "leo",
                registeredSubscription = null,
                registerSubscriptionForm = RegisterSubscriptionFormState(
                    firstName = "",
                    lastName = "",
                    dni = "",
                    address = "",
                    phone = "",
                    price = "",
                    subscriptionDate = 5666,
                    additionalDevices = listOf(),
                    selectedPlace = null,
                    technician = null,
                    hostDevice = null,
                    location = null,
                    cpeDevice = null,
                    selectedNapBox = null,
                    coupon = "mazim",
                    isMigration = false,
                    note = "persequeris"
                )
            )
        )
    }
}
