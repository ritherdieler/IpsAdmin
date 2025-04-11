package com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.entity.CustomerData
import com.example.cleanarchitecture.domain.entity.NapBoxResponse
import com.example.cleanarchitecture.domain.entity.PlaceResponse
import com.example.cleanarchitecture.domain.entity.ServiceStatus
import com.example.cleanarchitecture.domain.entity.SubscriptionResume
import com.example.data2.data.apirequestmodel.MoveOnuRequest
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val REQUEST_DELAY = 500L

data class SubscriptionFinderUiState(
    val subscriptions: Map<ServiceStatus, List<SubscriptionResume>> = emptyMap(),
    val cancelSubscriptionState: CancelSubscriptionState = CancelSubscriptionState.Empty,
    val saveSubscriptionState: SaveSubscriptionState = SaveSubscriptionState.Success,
    val napBoxesState: NapBoxesState = NapBoxesState.Loading,
    val placesState: PlacesState = PlacesState(),
    val selectedSubscription: SubscriptionResume? = null,
    val customerFormData: CustomerFormData? = null
)

data class CustomerFormData(
    val name: String = "",
    val lastName: String = "",
    val phone: String = "",
    val dni: String = "",
    val address: String = "",
    val email: String = "",
    val place: String = "",
    val placeId: Int = 0,
    val subscriptionId: Int = 0,
    val customerId: Int = 0
)

class SubscriptionFinderViewModel(
    private val repository: IRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionFinderUiState())
    val uiState: StateFlow<SubscriptionFinderUiState> = _uiState.asStateFlow()

    private val subscriptionsFlow = MutableStateFlow<List<SubscriptionResume>>(emptyList())

    val documentNumberFlow = MutableSharedFlow<SubscriptionFilter>(extraBufferCapacity = 1)

    init {
        findSubscription()
        getPlaces()

        // Observe subscriptionsFlow and update the UI state
        viewModelScope.launch {
            subscriptionsFlow.map { list ->
                list.map {
                    if (it.serviceStatus != ServiceStatus.CANCELLED) it.copy(serviceStatus = ServiceStatus.ACTIVE)
                    else it.copy(serviceStatus = ServiceStatus.CANCELLED)
                }.groupBy { it.serviceStatus }
            }.collect { groupedSubscriptions ->
                _uiState.update { it.copy(subscriptions = groupedSubscriptions) }
            }
        }
    }

    fun resetNapBoxFlow() {
        _uiState.update { it.copy(napBoxesState = NapBoxesState.Loading) }
    }

    @OptIn(FlowPreview::class)
    fun findSubscription() = viewModelScope.launch {
        documentNumberFlow.debounce(REQUEST_DELAY)
            .collect { filterType ->
                val response = when (filterType) {
                    is SubscriptionFilter.BY_DATE -> {
                        if (filterType.startDate.isEmpty() || filterType.endDate.isEmpty()) {
                            subscriptionsFlow.value = emptyList()
                            return@collect
                        }
                        repository.findSubscriptionBySubscriptionDate(
                            filterType.startDate,
                            filterType.endDate
                        )
                    }

                    is SubscriptionFilter.BY_DOCUMENT -> {
                        if (filterType.documentNumber.isEmpty()) {
                            subscriptionsFlow.value = emptyList()
                            return@collect
                        } else {
                            repository.findSubscriptionByDNI(filterType.documentNumber)
                        }
                    }

                    is SubscriptionFilter.BY_NAME -> {
                        if (filterType.name.isEmpty() && filterType.lastName.isEmpty()) {
                            subscriptionsFlow.value = emptyList()
                            return@collect
                        } else {
                            repository.findSubscriptionByNameAndLastName(
                                filterType.name,
                                filterType.lastName
                            )
                        }
                    }
                }
                subscriptionsFlow.value = response
            }
    }

    fun updateCustomerData(customer: CustomerData) = viewModelScope.launch {
        try {
            _uiState.update { it.copy(saveSubscriptionState = SaveSubscriptionState.Loading) }
            repository.updateCustomerData(customer)
            _uiState.update { it.copy(saveSubscriptionState = SaveSubscriptionState.Success) }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.update { it.copy(saveSubscriptionState = SaveSubscriptionState.Error) }
        }
    }

    fun setSelectedSubscription(subscription: SubscriptionResume?) {
        _uiState.update { it.copy(selectedSubscription = subscription) }
    }

    fun cancelSubscription(subscriptionId: Int) = viewModelScope.launch {
        try {
            _uiState.update { it.copy(cancelSubscriptionState = CancelSubscriptionState.Loading) }
            repository.cancelSubscription(subscriptionId)
            _uiState.update { it.copy(cancelSubscriptionState = CancelSubscriptionState.Success) }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.update { it.copy(cancelSubscriptionState = CancelSubscriptionState.Error) }
        }
    }

    fun removeSubscriptionFromList(id: Int) {
        subscriptionsFlow.value = subscriptionsFlow.value.filter { it.id != id }
        _uiState.update { it.copy(cancelSubscriptionState = CancelSubscriptionState.Empty) }
    }

    fun getNapBoxes() = viewModelScope.launch {
        try {
            _uiState.update { it.copy(napBoxesState = NapBoxesState.Loading) }
            val response = repository.getNapBoxes()
            _uiState.update { it.copy(napBoxesState = NapBoxesState.NapBoxListLoaded(response)) }
        } catch (e: Exception) {
            _uiState.update { it.copy(napBoxesState = NapBoxesState.Error) }
            e.printStackTrace()
        }
    }

    fun changeNapBox(request: MoveOnuRequest) = viewModelScope.launch {
        try {
            _uiState.update { it.copy(napBoxesState = NapBoxesState.Loading) }
            repository.changeSubscriptionNapBox(request)
            _uiState.update { it.copy(napBoxesState = NapBoxesState.NapBoxChanged) }
        } catch (e: Exception) {
            _uiState.update { it.copy(napBoxesState = NapBoxesState.Error) }
            e.printStackTrace()
        }
    }

    private fun getPlaces() = viewModelScope.launch {
        try {
            _uiState.update { it.copy(placesState = it.placesState.copy(isLoading = true)) }
            val places = repository.getPlaces()
            _uiState.update {
                it.copy(
                    placesState = it.placesState.copy(
                        places = places,
                        isLoading = false
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.update {
                it.copy(
                    placesState = it.placesState.copy(
                        isLoading = false,
                        error = e.message
                    )
                )
            }
        }
    }

    fun onPlaceSelected(place: PlaceResponse) {
        _uiState.update {
            it.copy(
                placesState = it.placesState.copy(selectedPlace = place)
            )
        }
    }

    fun initCustomerFormData(subscription: SubscriptionResume) {
        val customer = subscription.customer

        // First, find and select the current place in placesState
        val currentPlace = _uiState.value.placesState.places.find { place ->
            place.id?.toIntOrNull() == subscription.placeId.toInt()
        }

        // Update place selection first
        if (currentPlace != null) {
            onPlaceSelected(currentPlace)
        }

        // Then update the form data
        _uiState.update { currentState ->
            currentState.copy(
                customerFormData = CustomerFormData(
                    name = customer.name,
                    lastName = customer.lastName,
                    phone = customer.phone,
                    dni = customer.dni,
                    address = customer.address,
                    email = customer.email,
                    place = customer.place,
                    placeId = customer.placeId ?: 0,
                    subscriptionId = subscription.id,
                    customerId = customer.subscriptionId // Using subscriptionId as customerId
                )
            )
        }
    }

    fun updateCustomerFormField(field: String, value: String) {
        _uiState.value.customerFormData?.let { formData ->
            val updatedFormData = when (field) {
                "name" -> formData.copy(name = value)
                "lastName" -> formData.copy(lastName = value)
                "phone" -> formData.copy(phone = value)
                "dni" -> formData.copy(dni = value)
                "address" -> formData.copy(address = value)
                "email" -> formData.copy(email = value)
                "place" -> formData.copy(place = value)
                else -> formData
            }
            _uiState.update { it.copy(customerFormData = updatedFormData) }
        }
    }

    fun updateCustomerPlaceId(placeId: Int, placeName: String) {
        _uiState.value.customerFormData?.let { formData ->
            val updatedFormData = formData.copy(
                placeId = placeId,
                place = placeName
            )
            _uiState.update { it.copy(customerFormData = updatedFormData) }
        }
    }

    fun saveCustomerData() = viewModelScope.launch {
        try {
            _uiState.update { it.copy(saveSubscriptionState = SaveSubscriptionState.Loading) }

            val formData = _uiState.value.customerFormData ?: return@launch

            val customerData = CustomerData(
                subscriptionId = formData.subscriptionId,
                name = formData.name,
                lastName = formData.lastName,
                phone = formData.phone,
                dni = formData.dni,
                address = formData.address,
                email = formData.email,
                place = formData.place,
                placeId = formData.placeId
            )

            repository.updateCustomerData(customerData)
            _uiState.update {

                subscriptionsFlow.value = subscriptionsFlow.value.map { subscription ->
                    if (subscription.id == formData.subscriptionId) {
                        subscription.copy(
                            customer = customerData,
                            placeId = formData.placeId.toString()
                        )
                    } else {
                        subscription
                    }
                }

                it.copy(saveSubscriptionState = SaveSubscriptionState.Success)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.update { it.copy(saveSubscriptionState = SaveSubscriptionState.Error) }
        }
    }
}

sealed class SaveSubscriptionState {
    object Loading : SaveSubscriptionState()
    object Success : SaveSubscriptionState()
    object Error : SaveSubscriptionState()
}

sealed class CancelSubscriptionState {
    object Empty : CancelSubscriptionState()
    object Loading : CancelSubscriptionState()
    object Success : CancelSubscriptionState()
    object Error : CancelSubscriptionState()
}

sealed class NapBoxesState {
    object Loading : NapBoxesState()
    data class NapBoxListLoaded(val items: List<NapBoxResponse>) : NapBoxesState()
    object NapBoxChanged : NapBoxesState()
    object Error : NapBoxesState()
}

data class PlacesState(
    val places: List<PlaceResponse> = emptyList(),
    val selectedPlace: PlaceResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)