package com.dscorp.ispadmin.presentation.ui.features.subscription.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FieldValidator
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FormField
import com.example.cleanarchitecture.domain.domain.entity.PlanResponse
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.example.data2.data.apirequestmodel.UpdateSubscriptionPlanBody
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class EditSubscriptionViewModel(
    private val repository: IRepository,
) : ViewModel() {

    val editSubscriptionUiState = MutableSharedFlow<EditSubscriptionUiState>()
    var subscription: SubscriptionResponse? = null
    val user = repository.getUserSession()
    val planField = FormField(
        hintResourceId = R.string.plan,
        errorResourceId = R.string.mustSelectPlan,
        fieldValidator = object : FieldValidator<PlanResponse?> {
            override fun validate(fieldValue: PlanResponse?): Boolean = fieldValue != null
        }
    )

    fun getFormData() = viewModelScope.launch {
        try {
            val plansJob = async { repository.getPlans() }
            val plans = plansJob.await()
            editSubscriptionUiState.emit(
                EditSubscriptionUiState.EditFormDataFound(
                    plans
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            editSubscriptionUiState.emit(EditSubscriptionUiState.FormDataError(e.message.toString()))
        }
    }

    fun editarSubscription() = viewModelScope.launch {
        try {
            if (!formIsValid()) return@launch
            val subscription = createSubscription()
            val subscriptionFromRepository = repository.editSubscription(subscription)
            editSubscriptionUiState.emit(
                EditSubscriptionUiState.EditSubscriptionSuccess(subscriptionFromRepository)
            )
        } catch (error: Exception) {
            editSubscriptionUiState.emit(EditSubscriptionUiState.EditSubscriptionError(error.message.toString()))
        }
    }

    private fun createSubscription(): UpdateSubscriptionPlanBody {
        if (!formIsValid()) throw Exception("Form is not valid")

        return UpdateSubscriptionPlanBody(
            subscriptionId = subscription?.id!!,
            planId = planField.value?.id!!,
        )
    }

    private fun formIsValid(): Boolean {

        val fields = listOf(planField)
        for (field in fields) {
            field.isValid
        }
        return fields.all { it.isValid }
    }
}
