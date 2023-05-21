package com.dscorp.ispadmin.presentation.ui.features.subscription.edit

import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FieldValidator
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FormField
import com.example.cleanarchitecture.domain.domain.entity.PlanResponse
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.example.data2.data.apirequestmodel.UpdateSubscriptionPlanBody
import com.example.data2.data.repository.IRepository

class EditSubscriptionViewModel(
    private val repository: IRepository,
) : BaseViewModel<EditSubscriptionUiState>() {

    var subscription: SubscriptionResponse? = null
    val user = repository.getUserSession()
    val planField = FormField(
        hintResourceId = R.string.plan,
        errorResourceId = R.string.mustSelectPlan,
        fieldValidator = object : FieldValidator<PlanResponse?> {
            override fun validate(fieldValue: PlanResponse?): Boolean = fieldValue != null
        }
    )

    fun getFormData() = executeWithProgress {
        val response = repository.getPlans()
        uiState.value = BaseUiState(EditSubscriptionUiState.EditFormDataFound(response))
    }

    fun editSubscription() = executeWithProgress {
        if (!formIsValid()) return@executeWithProgress
        val subscription = createSubscription()
        val subscriptionFromRepository = repository.updateSubscriptionPlan(subscription)
        uiState.value =
            BaseUiState(EditSubscriptionUiState.EditSubscriptionSuccess(subscriptionFromRepository))

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
