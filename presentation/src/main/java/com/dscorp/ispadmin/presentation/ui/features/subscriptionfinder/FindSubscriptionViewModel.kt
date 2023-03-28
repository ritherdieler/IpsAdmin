package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FieldValidator
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.formvalidation.FormField
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FindSubscriptionViewModel : ViewModel(), KoinComponent {
    val repository: IRepository by inject()
    val uiStateLiveData = MutableLiveData<FindSubscriptionUiState>()
    val loadingUiState = MutableLiveData(false)

    var searchType = MutableLiveData(SearchType.BY_DNI)


    val dniField =
        FormField(R.string.digit_dni, R.string.invalidDNI, object : FieldValidator<String> {
            override fun validate(fieldValue: String?) = fieldValue?.length == 8
        })

    val startDateField = FormField<Long?>(
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

    fun findSubscriptionByDni() = viewModelScope.launch {
        if (!dniField.isValid) return@launch

        loadingUiState.postValue(true)
        try {
            val response = repository.findSubscriptionByDNI(dniField.value!!)
            uiStateLiveData.postValue(FindSubscriptionUiState.OnSubscriptionFound(response))
        } catch (e: Exception) {
            e.printStackTrace()
            uiStateLiveData.postValue(FindSubscriptionUiState.OnError(e.message))
        } finally {
            loadingUiState.postValue(false)
        }
    }

    private fun findSubscriptionsBySubscriptionDate() = viewModelScope.launch {
        if (!startDateField.isValid || !endDateField.isValid) return@launch

        loadingUiState.postValue(true)
        try {
            val response = repository.findSubscriptionBySubscriptionDate(
                startDateField.value!!,
                endDateField.value!!
            )
            uiStateLiveData.postValue(FindSubscriptionUiState.OnSubscriptionFound(response))
        } catch (e: Exception) {
            e.printStackTrace()
            uiStateLiveData.postValue(FindSubscriptionUiState.OnError(e.message))
        } finally {
            loadingUiState.postValue(false)
        }

    }

    fun onSearchTypeChanged(button: View, isChecked: Boolean) {
        if (!isChecked) return
        when (button.id) {
            R.id.rbBySubscriptionDate -> searchType.value = SearchType.BY_SUBSCRIPTION_DATE
            R.id.rbByDni -> searchType.value = SearchType.BY_DNI
            else -> {}
        }
    }

    enum class SearchType {
        BY_DNI,
        BY_SUBSCRIPTION_DATE
    }

}
