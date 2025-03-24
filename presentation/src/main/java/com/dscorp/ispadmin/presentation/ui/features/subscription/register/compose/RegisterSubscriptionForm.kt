package com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose

import MyTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onDniChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onPriceChanged: (String) -> Unit,
    onPlanSelected: (PlanResponse) -> Unit,
    onOnuSelected: (Onu) -> Unit,
    onPlaceSelected: (PlaceResponse) -> Unit,
    onNapBoxSelected: (NapBoxResponse) -> Unit
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column {

            MyFormRow(modifier = Modifier.fillMaxWidth()) {
                MyOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    onValueChange = onFirstNameChanged,
                    value = formState.registerSubscriptionForm.firstName,
                    label = "Nombres",
                    errorMessage = formState.registerSubscriptionForm.firstNameError
                )

                MyOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    onValueChange = onLastNameChanged,
                    value = formState.registerSubscriptionForm.lastName,
                    label = "Apellidos",
                    errorMessage = formState.registerSubscriptionForm.lastNameError
                )
            }

            MyFormRow {
                MyOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    onValueChange = onDniChanged,
                    value = formState.registerSubscriptionForm.dni,
                    label = "Dni",
                    errorMessage = formState.registerSubscriptionForm.dniError
                )
                MyOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    onValueChange = onAddressChanged,
                    value = formState.registerSubscriptionForm.address,
                    label = "Direccion",
                    errorMessage = formState.registerSubscriptionForm.addressError
                )
            }

            MyFormRow {
                MyOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    onValueChange = onPhoneChanged,
                    value = formState.registerSubscriptionForm.phone,
                    label = "Telefono",
                    errorMessage = formState.registerSubscriptionForm.phoneError
                )
                MyOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    onValueChange = onPriceChanged,
                    value = formState.registerSubscriptionForm.price,
                    label = "Precio",
                    errorMessage = formState.registerSubscriptionForm.priceError
                )
            }

            MyFormRow {
                MyOutLinedDropDown(
                    label = "Plan",
                    modifier = Modifier.weight(1f),
                    items = formState.registerSubscriptionForm.planList,
                    onItemSelected = onPlanSelected
                )

                MyOutLinedDropDown(
                    label = "Lugar",
                    modifier = Modifier.weight(1f),
                    items = formState.registerSubscriptionForm.placeList,
                    selected = formState.registerSubscriptionForm.selectedPlace,
                    onItemSelected = onPlaceSelected
                )

            }

            MyFormRow {
                MyOutLinedDropDown(
                    label = "Onu",
                    modifier = Modifier.weight(1f),
                    items = formState.registerSubscriptionForm.onuList,
                    onItemSelected = onOnuSelected
                )

                MyOutLinedDropDown(
                    label = "Caja Nap",
                    modifier = Modifier.weight(1f),
                    items = formState.registerSubscriptionForm.napBoxList,
                    onItemSelected = onNapBoxSelected
                )


            }
        }

    }
}


@Composable
fun MyFormRow(
    modifier: Modifier = Modifier,
    horizontalGap: Dp = 8.dp,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(horizontalGap)
    ) {
        content()
    }
}

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
            ),
            onFirstNameChanged = {},
            onLastNameChanged = {},
            onDniChanged = {},
            onAddressChanged = {},
            onPhoneChanged = {},
            onPriceChanged = {},
            onPlanSelected = {},
            onOnuSelected = {},
            onPlaceSelected = {},
            onNapBoxSelected = {}
        )
    }
}