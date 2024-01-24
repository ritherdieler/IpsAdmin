package com.dscorp.ispadmin.presentation.ui.features.subscriptiondetail.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import com.dscorp.ispadmin.presentation.ui.features.composecomponents.MyDropDown
import com.dscorp.ispadmin.presentation.ui.features.subscriptiondetail.SubscriptionDetailViewModel
import com.example.cleanarchitecture.domain.domain.entity.extensions.toFormattedDateString
import org.koin.androidx.compose.koinViewModel

@Composable
fun SubscriptionDetailForm(
    subscriptionId: Int,
    viewModel: SubscriptionDetailViewModel = koinViewModel()
) {
    val scrollState = rememberScrollState()
    val places by viewModel.places.asFlow().collectAsState(initial = listOf())


    LaunchedEffect(places) {
        viewModel.initForm(subscriptionId)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {

        Row {
            OutlinedTextField(
                value = viewModel.editSubscriptionForm.firstNameField.liveData.value ?: "",
                onValueChange = {
                    viewModel.editSubscriptionForm.firstNameField.liveData.value = it
                },
                label = { Text(text = viewModel.editSubscriptionForm.firstNameField.hint ?: "") },
                modifier = Modifier.weight(1f),
                isError = viewModel.editSubscriptionForm.firstNameField.errorLiveData.value != null,
                singleLine = true,
                readOnly = true,
                maxLines = 1
            )

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedTextField(
                value = viewModel.editSubscriptionForm.lastNameField.liveData.value ?: "",
                onValueChange = {
                    viewModel.editSubscriptionForm.lastNameField.liveData.value = it
                },
                readOnly = true,
                label = { Text(text = viewModel.editSubscriptionForm.lastNameField.hint ?: "") },
                modifier = Modifier.weight(1f),
                isError = viewModel.editSubscriptionForm.lastNameField.errorLiveData.value != null,
                singleLine = true,
                maxLines = 1
            )
        }

        Row {

            OutlinedTextField(
                value = viewModel.editSubscriptionForm.dniField.liveData.value ?: "",
                onValueChange = { viewModel.editSubscriptionForm.dniField.liveData.value = it },
                label = { Text(text = viewModel.editSubscriptionForm.dniField.hint ?: "") },
                modifier = Modifier.weight(1f),
                isError = viewModel.editSubscriptionForm.dniField.errorLiveData.value != null,
                singleLine = true,
                readOnly = true,
                maxLines = 1
            )

            Spacer(modifier = Modifier.width(16.dp)) // Add space between the two fields

            OutlinedTextField(
                value = viewModel.editSubscriptionForm.addressField.liveData.value ?: "",
                onValueChange = { viewModel.editSubscriptionForm.addressField.liveData.value = it },
                label = { Text(text = viewModel.editSubscriptionForm.addressField.hint ?: "") },
                modifier = Modifier.weight(1f),
                isError = viewModel.editSubscriptionForm.addressField.errorLiveData.value != null,
                singleLine = true,
                readOnly = true,
                maxLines = 1
            )
        }

        Row {

            OutlinedTextField(
                value = viewModel.editSubscriptionForm.phoneField.liveData.value ?: "",
                onValueChange = { viewModel.editSubscriptionForm.phoneField.liveData.value = it },
                label = { Text(text = viewModel.editSubscriptionForm.phoneField.hint ?: "") },
                modifier = Modifier.weight(1f),
                isError = viewModel.editSubscriptionForm.phoneField.errorLiveData.value != null,
                singleLine = true,
                readOnly = true,
                maxLines = 1
            )

            Spacer(modifier = Modifier.width(16.dp)) // Add space between the two fields

            OutlinedTextField(
                value = viewModel.editSubscriptionForm.couponField.liveData.value ?: "",
                onValueChange = { viewModel.editSubscriptionForm.couponField.liveData.value = it },
                label = { Text(text = viewModel.editSubscriptionForm.couponField.hint ?: "") },
                modifier = Modifier.weight(1f),
                isError = viewModel.editSubscriptionForm.couponField.errorLiveData.value != null,
                singleLine = true,
                readOnly = true,
                maxLines = 1
            )

        }

        Row(verticalAlignment = Alignment.CenterVertically) {


            OutlinedTextField(
                value = viewModel.editSubscriptionForm.planField.liveData.value?.name ?: "",
                onValueChange = {},
                label = { Text(text = viewModel.editSubscriptionForm.planField.hint ?: "") },
                modifier = Modifier.weight(1f),
                isError = viewModel.editSubscriptionForm.planField.errorLiveData.value != null,
                singleLine = true,
                readOnly = true,
                maxLines = 1
            )

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedTextField(
                value = viewModel.editSubscriptionForm.placeField.liveData.value?.name ?: "",
                onValueChange = {},
                label = { Text(text = viewModel.editSubscriptionForm.placeField.hint ?: "") },
                modifier = Modifier.weight(1f),
                isError = viewModel.editSubscriptionForm.placeField.errorLiveData.value != null,
                singleLine = true,
                readOnly = true,
                maxLines = 1
            )

        }


        OutlinedTextField(
            value = viewModel.editSubscriptionForm.noteField.liveData.value ?: "",
            onValueChange = { viewModel.editSubscriptionForm.noteField.liveData.value = it },
            label = { Text(text = viewModel.editSubscriptionForm.noteField.hint ?: "") },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.editSubscriptionForm.noteField.errorLiveData.value != null,
            singleLine = true,
            readOnly = true,
            maxLines = 1
        )

        OutlinedTextField(
            value = viewModel.editSubscriptionForm.subscriptionDateField.liveData.value?.toFormattedDateString()
                ?: "",
            onValueChange = {

            },
            label = {
                Text(
                    text = viewModel.editSubscriptionForm.subscriptionDateField.hint ?: ""
                )
            },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.editSubscriptionForm.subscriptionDateField.errorLiveData.value != null,
            singleLine = true,
            readOnly = true,

            maxLines = 1
        )

        OutlinedTextField(
            value = viewModel.editSubscriptionForm.technicianField.liveData.value?.name ?: "",
            onValueChange = { },
            label = { Text(text = viewModel.editSubscriptionForm.technicianField.hint ?: "") },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.editSubscriptionForm.technicianField.errorLiveData.value != null,
            singleLine = true,
            readOnly = true,
            maxLines = 1
        )

        OutlinedTextField(
            value = viewModel.editSubscriptionForm.priceField.liveData.value ?: "",
            onValueChange = { viewModel.editSubscriptionForm.priceField.liveData.value = it },
            label = { Text(text = viewModel.editSubscriptionForm.priceField.hint ?: "") },
            isError = viewModel.editSubscriptionForm.priceField.errorLiveData.value != null,
            singleLine = true,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.editSubscriptionForm.ipField.liveData.value ?: "",
            onValueChange = { viewModel.editSubscriptionForm.ipField.liveData.value = it },
            label = { Text(text = viewModel.editSubscriptionForm.ipField.hint ?: "") },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.editSubscriptionForm.ipField.errorLiveData.value != null,
            singleLine = true,
            maxLines = 1,
            readOnly = true
        )

        OutlinedTextField(
            value = viewModel.editSubscriptionForm.hostDeviceField.liveData.value?.name ?: "",
            onValueChange = { },
            label = { Text(text = viewModel.editSubscriptionForm.hostDeviceField.hint ?: "") },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.editSubscriptionForm.hostDeviceField.errorLiveData.value != null,
            singleLine = true,
            readOnly = true,
            maxLines = 1
        )
    }


//        FloatingActionButton(
//            onClick = { viewModel.makeFieldsEditable() },
//            modifier = Modifier
//                .align(Alignment.End)
//                .padding(16.dp)
//        ) {
//            Icon(
//                painter = painterResource(
//                    id = viewModel.editingIcon.value ?: R.drawable.baseline_edit_24
//                ),
//                contentDescription = "Edit subscription data"
//            )

}
