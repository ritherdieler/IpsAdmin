package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.di.app.ResourceProvider
import com.dscorp.ispadmin.presentation.extension.asCalendar
import com.dscorp.ispadmin.presentation.extension.isLastDayOfMonth
import com.dscorp.ispadmin.presentation.extension.isSameMonthAndYear
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FieldValidator
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FormField
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.FindSubscriptionUiState.*
import com.example.cleanarchitecture.domain.domain.entity.ServiceStatus
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.example.cleanarchitecture.domain.domain.entity.User
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import java.util.Calendar

class FindSubscriptionViewModel(
    private val repository: IRepository,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<FindSubscriptionUiState>(), KoinComponent {
    val loadingUiState = MutableLiveData(false)

    var searchType = MutableLiveData(SearchType.BY_DNI)
    val user = repository.getUserSession()

    val dniField =
        FormField(R.string.digit_dni, R.string.invalidDNI, object : FieldValidator<String> {
            override fun validate(fieldValue: String?) = fieldValue?.length == 8
        })

    val startDateField = FormField(
        R.string.start_date,
        R.string.must_select_start_date,
        object : FieldValidator<Long?> {
            override fun validate(fieldValue: Long?) = fieldValue != null
        })

    val endDateField =
        FormField(R.string.end_date, R.string.must_select_end_date, object : FieldValidator<Long?> {
            override fun validate(fieldValue: Long?) = fieldValue != null
        })

    fun search() {
        when (searchType.value) {
            SearchType.BY_DNI -> findSubscriptionByDni()
            SearchType.BY_SUBSCRIPTION_DATE -> findSubscriptionsBySubscriptionDate()
            else -> {}
        }
    }

    fun findSubscriptionByDni() = executeWithProgress {
        if (!dniField.isValid) return@executeWithProgress
        val response = repository.findSubscriptionByDNI(dniField.value!!)
        uiState.value = BaseUiState(OnSubscriptionFound(response))
    }

    private fun findSubscriptionsBySubscriptionDate() = executeWithProgress {
        if (!startDateField.isValid || !endDateField.isValid) return@executeWithProgress
        val response = repository.findSubscriptionBySubscriptionDate(
            startDateField.value!!,
            endDateField.value!!
        )
        uiState.value = BaseUiState(OnSubscriptionFound(response))
    }

    fun onSearchTypeChanged(button: View, isChecked: Boolean) {
        if (!isChecked) return
        when (button.id) {
            R.id.rbBySubscriptionDate -> searchType.value = SearchType.BY_SUBSCRIPTION_DATE
            R.id.rbByDni -> searchType.value = SearchType.BY_DNI
            else -> {}
        }
    }

    fun savePaymentCommitment(subscription: SubscriptionResponse) = executeWithProgress {
        val currentDateCalendar = Calendar.getInstance()
        if (subscription.lastCutOffDate == null) throw Exception(resourceProvider.getString(R.string.subscription_has_not_service_cut_off))

        val lasCancellationDateCalendar =
            Calendar.getInstance().apply { timeInMillis = subscription.lastCutOffDate!! }
        when {
            !currentDateCalendar.isSameMonthAndYear(lasCancellationDateCalendar) -> {
                throw Exception(resourceProvider.getString(R.string.payment_commitment_cant_be_registered_in_different_month_tan_cut_off_date))
            }

            currentDateCalendar.isLastDayOfMonth() -> {
                throw Exception(resourceProvider.getString(R.string.payment_commitment_cant_create_at_last_day_of_month))
            }

            else -> {
                repository.savePaymentCommitment(subscription.id)
                uiState.value = BaseUiState(PaymentCommitmentSuccess)
            }
        }

    }

    fun filterMenuItems(subscription: SubscriptionResponse) {
        val currentCal = Calendar.getInstance()

        when (subscription.serviceStatus) {
            ServiceStatus.CUT_OFF -> handleCutOffUiState(subscription, currentCal)

            ServiceStatus.SUSPENDED ->
                uiState.value = BaseUiState(ShowReactivateServiceOption(true))

            else -> {}
        }

        user?.let {
            if (it.type == User.UserType.TECHNICIAN) {
                uiState.value = BaseUiState(ShowEditPlanOption(false))
                uiState.value = BaseUiState(ShowRegisterServiceOrder(false))
            }
        }
    }

    private fun handleCutOffUiState(
        subscription: SubscriptionResponse,
        currentCal: Calendar
    ) {
        subscription.lastCutOffDate?.asCalendar()?.let {
            val showCommitmentOption =
                it.isSameMonthAndYear(currentCal) && !currentCal.isLastDayOfMonth()
            uiState.value = BaseUiState(
                ShowPaymentCommitmentOption(showCommitmentOption)
            )
        }
    }


    enum class SearchType {
        BY_DNI,
        BY_SUBSCRIPTION_DATE
    }
}

sealed interface FindSubscriptionUiState {
    object PaymentCommitmentSuccess : FindSubscriptionUiState
    class ShowPaymentCommitmentOption(val showOption: Boolean) : FindSubscriptionUiState
    class ShowReactivateServiceOption(val showOption: Boolean) : FindSubscriptionUiState
    class OnSubscriptionFound(val subscriptions: List<SubscriptionResponse>) :
        FindSubscriptionUiState

    class ShowEditPlanOption(val showOption: Boolean) : FindSubscriptionUiState

    class ShowRegisterServiceOrder(val showOption: Boolean) :FindSubscriptionUiState
}
