package com.dscorp.ispadmin.presentation.ui.features.subscription.edit

import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FieldValidator
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FormField
import com.example.cleanarchitecture.domain.entity.PlanResponse
import com.example.cleanarchitecture.domain.entity.SubscriptionResponse
import com.example.data2.data.apirequestmodel.UpdateSubscriptionPlanBody
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.async


data class EditSubscriptionScreenData(
    val plans: List<PlanResponse>,
    val subscription: SubscriptionResponse
)

class EditSubscriptionViewModel(
    private val repository: IRepository,
) : BaseViewModel<EditSubscriptionUiState>() {

    val user = repository.getUserSession()
    val planField = FormField(
        hintResourceId = R.string.plan,
        errorResourceId = R.string.mustSelectPlan,
        fieldValidator = object : FieldValidator<PlanResponse?> {
            override fun validate(fieldValue: PlanResponse?): Boolean = fieldValue != null
        }
    )

    fun getFormData(subscriptionId: Int) = executeWithProgress {
        val jobSubscription = it.async { repository.subscriptionById(subscriptionId) }
        val jobPlans = it.async { repository.getPlans() }

        val subscriptionResponse = jobSubscription.await()
        val plans = jobPlans.await()

        uiState.value = BaseUiState(
            EditSubscriptionUiState.EditFormDataFound(plans, subscriptionResponse)
        )
    }

    fun editSubscription(subscriptionId: Int) = executeWithProgress {
        if (!formIsValid()) return@executeWithProgress
        val subscription = createSubscription(subscriptionId)
        val subscriptionFromRepository = repository.updateSubscriptionPlan(subscription)
        uiState.value =
            BaseUiState(EditSubscriptionUiState.EditSubscriptionSuccess(subscriptionFromRepository))

    }

    private fun createSubscription(subscriptionId: Int): UpdateSubscriptionPlanBody {
        if (!formIsValid()) throw Exception("Form is not valid")

        return UpdateSubscriptionPlanBody(
            subscriptionId = subscriptionId,
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
