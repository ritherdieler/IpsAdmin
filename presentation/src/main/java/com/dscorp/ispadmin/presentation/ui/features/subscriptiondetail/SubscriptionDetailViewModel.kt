package com.dscorp.ispadmin.presentation.ui.features.subscriptiondetail

import androidx.lifecycle.MutableLiveData
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.di.app.ResourceProvider
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.dscorp.ispadmin.presentation.ui.features.forms.subscription.EditSubscriptionDataForm
import com.example.cleanarchitecture.domain.domain.entity.PlaceResponse
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.example.data2.data.repository.IRepository

class SubscriptionDetailViewModel(
    val editSubscriptionForm: EditSubscriptionDataForm,
    val repository: IRepository,
    val resourceProvider: ResourceProvider
) :
    BaseViewModel<SubscriptionDetailUiState>() {

    var isEditingForm = false
    val editingIcon = MutableLiveData(R.drawable.baseline_edit_24)

    val places = MutableLiveData<List<PlaceResponse>>()
    fun initForm(subscription: SubscriptionResponse) {
        editSubscriptionForm.initForm(subscription)
        getPlaces()
    }

    private fun getPlaces() {
        executeNoProgress {
            val response = repository.getPlaces()
            places.value = response
        }
    }

    fun makeFieldsEditable() {
        if (!isEditingForm) {
            isEditingForm = true
            editingIcon.value = R.drawable.baseline_check_24
            editSubscriptionForm.changeEditableStatus(true)
        } else {
            updateSubscriptionData()
        }
    }

    private fun updateSubscriptionData() {
        executeWithProgress {
            editSubscriptionForm.getUpdateSubscriptionBody()?.let {
                repository.updateSubscriptionData(it)
                uiState.value = BaseUiState(SubscriptionDetailUiState.SubscriptionUpdated)
            }

        }
    }
}

sealed class SubscriptionDetailUiState {
    object SubscriptionUpdated : SubscriptionDetailUiState()

}
