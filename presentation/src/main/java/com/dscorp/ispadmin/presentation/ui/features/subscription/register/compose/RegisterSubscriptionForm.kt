package com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose

import MyTheme
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dscorp.ispadmin.presentation.composeComponents.MyAutoCompleteTextViewCompose
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyButton
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyIconButton
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyOutLinedDropDown
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyOutlinedTextField
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.models.RegisterSubscriptionFormState
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.models.RegisterSubscriptionState
import com.example.cleanarchitecture.domain.entity.InstallationType
import com.example.cleanarchitecture.domain.entity.NapBoxResponse
import com.example.cleanarchitecture.domain.entity.Onu
import com.example.cleanarchitecture.domain.entity.PlaceResponse
import com.example.cleanarchitecture.domain.entity.PlanResponse

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
    onInstallationTypeSelected: (InstallationType) -> Unit = {},
    onRefreshOnuList: () -> Unit = {},
    onNoteChanged: (String) -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
    ) {
        val scrollState = rememberScrollState()

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            FormRow {
                MyOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = formState.registerSubscriptionForm.firstName,
                    label = FIRST_NAME_LABEL,
                    onValueChange = onFirstNameChanged,
                    hasError = formState.registerSubscriptionForm.firstNameError != null,
                    singleLine = false,
                    maxLines = 4,
                )
                MyOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = formState.registerSubscriptionForm.lastName,
                    label = LAST_NAME_LABEL,
                    onValueChange = onLastNameChanged,
                    hasError = formState.registerSubscriptionForm.lastNameError != null,
                    singleLine = false,
                    maxLines = 4,
                )
            }

            FormRow {
                MyOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = formState.registerSubscriptionForm.dni,
                    label = DNI_LABEL,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    onValueChange = onDniChanged,
                    hasError = formState.registerSubscriptionForm.dniError != null,
                    singleLine = false,
                    maxLines = 4,

                    )
                MyOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = formState.registerSubscriptionForm.phone,
                    label = PHONE_LABEL,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    onValueChange = onPhoneChanged,
                    hasError = formState.registerSubscriptionForm.phoneError != null,
                    singleLine = false,
                    maxLines = 4,

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

                Text(
                    text = "Lugar seleccionado: ${formState.registerSubscriptionForm.selectedPlace?.toString() ?: "Ninguno"}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, bottom = 8.dp),
                    textAlign = TextAlign.Start,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                )

            MyOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = formState.registerSubscriptionForm.address,
                label = ADDRESS_LABEL,
                onValueChange = onAddressChanged,
                hasError = formState.registerSubscriptionForm.addressError != null,
                singleLine = false,
                maxLines = 4,

                )

            InstallationTypeSelector(
                installationType = formState.registerSubscriptionForm.installationType,
                onTypeSelected = {
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

            if (formState.registerSubscriptionForm.installationType == InstallationType.FIBER) {
                FiberOpticForm(
                    formState = formState,
                    onOnuSelected = onOnuSelected,
                    onNapBoxSelected = onNapBoxSelected,
                    onNapBoxSelectionCleared = onNapBoxSelectionCleared,
                    onRefreshOnuList = onRefreshOnuList
                )
            }

            MyOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onNoteChanged,
                value = formState.registerSubscriptionForm.note,
                label = NOTE_LABEL,
                singleLine = false,
                maxLines = 4,
                supportingText = {
                    Text(
                        text = "${formState.registerSubscriptionForm.note.length}/180",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            MyButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Registrar",
                onClick = onRegisterClick,
                enabled = formState.registerSubscriptionForm.isValid(),
                isLoading = formState.isLoading
            )
        }
    }
}

@Composable
fun InstallationTypeSelector(
    installationType: InstallationType,
    onTypeSelected: (InstallationType) -> Unit
) {
    Row {
        RadioButtonWithLabel(
            modifier = Modifier.weight(1f),
            label = FIBER_OPTIC,
            selected = installationType == InstallationType.FIBER,
            onClick = { onTypeSelected(InstallationType.FIBER) }
        )
        RadioButtonWithLabel(
            modifier = Modifier.weight(1f),
            label = WIRELESS,
            selected = installationType == InstallationType.WIRELESS,
            onClick = { onTypeSelected(InstallationType.WIRELESS) }
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
    onNapBoxSelectionCleared: () -> Unit,
    onRefreshOnuList: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        MyOutLinedDropDown(
            modifier = Modifier.weight(1f),
            items = formState.registerSubscriptionForm.onuList,
            selected = formState.registerSubscriptionForm.selectedOnu,
            label = ONU_LABEL,
            onItemSelected = onOnuSelected,
            hasError = formState.registerSubscriptionForm.onuError != null
        )

        RefreshIcon(onRefreshOnuList, formState)

    }

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
private fun RefreshIcon(
    onRefreshOnuList: () -> Unit,
    formState: RegisterSubscriptionState
) {
    val infiniteTransition = rememberInfiniteTransition(label = "refreshAnimation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAnimation"
    )

    MyIconButton(
        modifier = Modifier.padding(start = 8.dp),
        onClick = onRefreshOnuList
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "",
            modifier = Modifier.rotate(if (formState.isRefreshingOnuList) rotation else 0f)
        )
    }
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
private const val NOTE_LABEL = "Nota"
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
                    selectedPlace = null,
                    selectedHostDevice = null,
                    location = null,
                    cpeDevice = null,
                    selectedNapBox = null,
                    coupon = "mazim",
                    note = "persequeris"
                )
            )
        )
    }
}
