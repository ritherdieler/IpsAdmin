package com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.domain.entity.NapBoxResponse
import com.example.cleanarchitecture.domain.domain.entity.NetworkDevice
import com.example.cleanarchitecture.domain.domain.entity.Onu
import com.example.cleanarchitecture.domain.domain.entity.PlaceResponse
import com.example.cleanarchitecture.domain.domain.entity.PlanResponse
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.example.cleanarchitecture.domain.domain.entity.Technician
import com.example.cleanarchitecture.domain.domain.entity.extensions.isAValidAddress
import com.example.cleanarchitecture.domain.domain.entity.extensions.isAValidName
import com.example.cleanarchitecture.domain.domain.entity.extensions.isValidDni
import com.example.cleanarchitecture.domain.domain.entity.extensions.isValidPhone
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterSubscriptionComposeViewModel(
    private val getAvailableOnuListUseCase: GetAvailableOnuListUseCase,
    private val getPlanListUseCase: GetPlanListUseCase,
    private val getPlaceListUseCase: GetPlaceListUseCase,
    private val getPlaceFromLocationUseCase: GetPlaceFromLocationUseCase,
    private val getNapBoxListUseCase: GetNapBoxListUseCase
) : ViewModel() {

    val myUiState = MutableStateFlow(RegisterSubscriptionState())

    var myNapBoxList: List<NapBoxResponse> = emptyList()
        private set

    var myPlanList: List<PlanResponse> = emptyList()
        private set

    fun getFormData() = viewModelScope.launch {
        myUiState.update {
            it.copy(isLoading = true)
        }

        try {
            val onuListDeferred = async { getAvailableOnuListUseCase() }
            val planListDeferred = async { getPlanListUseCase() }
            val placeListDeferred = async { getPlaceListUseCase() }
            val napBoxListDeferred = async { getNapBoxListUseCase() }

            val onuList = onuListDeferred.await()
            val planList = planListDeferred.await()
            val placeList = placeListDeferred.await()
            val napBoxList = napBoxListDeferred.await()

            myNapBoxList = napBoxList.getOrNull() ?: emptyList()
            myPlanList = planList.getOrNull() ?: emptyList()

            myUiState.update {
                it.copy(
                    isLoading = false,
                    registerSubscriptionForm = it.registerSubscriptionForm.copy(
                        onuList = onuList.getOrNull() ?: emptyList(),
                        planList = myPlanList.filter { it.type == PlanResponse.PlanType.FIBER },
                        placeList = placeList.getOrNull() ?: emptyList(),
                        napBoxList = myNapBoxList,
                    )
                )
            }
        } catch (e: Exception) {
            myUiState.update {
                it.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }


    fun onFirstNameChanged(value: String) {
        updateFormField(
            value = value,
            isValid = { value.isAValidName() },
            errorMessage = "Nombre inválido",
            updateState = { firstName ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            firstName = firstName
                        )
                    )
                }

            },
            updateError = { error ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            firstNameError = error
                        )
                    )
                }
            }
        )
    }

    fun onLastNameChanged(value: String) {
        updateFormField(
            value = value,
            isValid = { value.isAValidName() },
            errorMessage = "Apellido inválido",
            updateState = { validLastName ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            lastName = validLastName
                        )
                    )
                }
            },
            updateError = { error ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            lastNameError = error
                        )
                    )
                }
            }
        )
    }

    fun onDniChanged(value: String) {
        updateFormField(
            value = value,
            isValid = { value.isValidDni() },
            errorMessage = "DNI inválido",
            updateState = { dni ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            dni = dni
                        )
                    )
                }
            },
            updateError = { error ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            dniError = error
                        )
                    )
                }
            }
        )
    }

    fun onAddressChanged(value: String) {
        updateFormField(
            value = value,
            isValid = { value.isAValidAddress() },
            errorMessage = "Dirección inválida",
            updateState = { address ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            address = address
                        )
                    )
                }
            },
            updateError = { error ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            addressError = error
                        )
                    )
                }
            }
        )
    }

    fun onPhoneChanged(value: String) {
        updateFormField(
            value = value,
            isValid = { value.isValidPhone() },
            errorMessage = "Número de teléfono inválido",
            updateState = { validPhoneNumber ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            phone = validPhoneNumber
                        )
                    )
                }
            },
            updateError = { error ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            phoneError = error
                        )
                    )
                }
            }
        )
    }

    fun onPlanSelected(value: PlanResponse) {
        updateFormField(
            value = value,
            isValid = { value != null },
            errorMessage = "Debe seleccionar un plan",
            updateState = { plan ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            selectedPlan = plan,
                            planError = null
                        )
                    )
                }
            },
            updateError = { error ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            planError = error
                        )
                    )
                }
            }
        )
    }

    fun onOnuSelected(value: Onu) {
        updateFormField(
            value = value,
            isValid = { value != null },
            errorMessage = "Debe seleccionar un ONU",
            updateState = { onu ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            selectedOnu = onu
                        )
                    )
                }
            },
            updateError = { error ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            onuError = error
                        )
                    )
                }
            }
        )
    }


    fun onPlaceSelected(value: PlaceResponse) {
        updateFormField(
            value = value,
            isValid = { value != null },
            errorMessage = "Debe seleccionar un lugar",
            updateState = { place ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            selectedPlace = place,
                            napBoxList = myNapBoxList.filter { it.placeId == place.id!!.toInt() },
                        )
                    )
                }
            },
            updateError = { error ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            placeError = error
                        )
                    )
                }
            }
        )
    }

    private fun <T> updateFormField(
        value: T,
        isValid: () -> Boolean,
        errorMessage: String?,
        updateState: (T) -> Unit,
        updateError: (String?) -> Unit
    ) {
        updateError(null)
        updateState(value)
        if (!isValid()) updateError(errorMessage)
    }

    fun getPlaceFromCurrentLocation(latitude: Double, longitude: Double) = viewModelScope.launch {

        getPlaceFromLocationUseCase(latitude, longitude).fold(
            onSuccess = { place ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            selectedPlace = place
                        )
                    )
                }
            },
            onFailure = { error ->
                myUiState.update {
                    it.copy(
                        error = error.message ?: "Unknown error"
                    )
                }
            }
        )

    }

    fun onNapBoxSelected(value: NapBoxResponse) {
        updateFormField(
            value = value,
            isValid = { value != null },
            errorMessage = "Debe seleccionar un NapBox",
            updateState = { napBox ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            selectedNapBox = napBox
                        )
                    )
                }
            },
            updateError = { error ->
                myUiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            napBoxError = error
                        )
                    )
                }
            }
        )
    }

    fun onPlaceSelectionCleared() {
        myUiState.update {
            it.copy(
                registerSubscriptionForm = it.registerSubscriptionForm.copy(
                    selectedPlace = null,
                    placeError = null
                )
            )
        }
    }

    fun onNapBoxSelectionCleared() {
        myUiState.update {
            it.copy(
                registerSubscriptionForm = it.registerSubscriptionForm.copy(
                    selectedNapBox = null,
                    napBoxError = null
                )
            )
        }
    }

    fun onInstallationTypeSelected(type: String) {
        val filteredPlans = when (type) {
            FIBER_OPTIC -> myPlanList.filter { it.type == PlanResponse.PlanType.FIBER }
            else -> myPlanList.filter { it.type == PlanResponse.PlanType.WIRELESS }
        }

        myUiState.update {
            it.copy(
                registerSubscriptionForm = it.registerSubscriptionForm.copy(
                    planList = filteredPlans,
                    selectedPlan = null, // Importante: establecer selectedPlan a null cuando cambia el tipo
                )
            )
        }
    }

    fun saveSubscription() {
        if(!myUiState.value.registerSubscriptionForm.isValid()) {
            myUiState.update {
                it.copy(
                    error = "Formulario inválido"
                )
            }
            return
        }


    }

}

data class RegisterSubscriptionState(
    val isLoading: Boolean = false,
    val error: String = "",
    val registeredSubscription: SubscriptionResponse? = null,
    val registerSubscriptionForm: RegisterSubscriptionFormState = RegisterSubscriptionFormState()
)


data class RegisterSubscriptionFormState(
    val firstName: String = "",
    val firstNameError: String? = null,
    val lastName: String = "",
    val lastNameError: String? = null,
    val dni: String = "",
    val dniError: String? = null,
    val address: String = "",
    val addressError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val price: String = "",
    val priceError: String? = null,
    val subscriptionDate: Long = 0,
    val planList: List<PlanResponse> = emptyList(),
    val selectedPlan: PlanResponse? = null,
    val planError: String? = null,
    val additionalDevices: List<NetworkDevice> = emptyList(),
    val placeList: List<PlaceResponse> = emptyList(),
    val selectedPlace: PlaceResponse? = null,
    val placeError: String? = null,
    val technician: Technician? = null,
    val hostDevice: NetworkDevice? = null,
    val location: LatLng? = null,
    val cpeDevice: NetworkDevice? = null,
    val napBoxError: String? = null,
    val napBoxList: List<NapBoxResponse> = emptyList(),
    val selectedNapBox: NapBoxResponse? = null,
    val onuList: List<Onu> = emptyList(),
    val selectedOnu: Onu? = null,
    val onuError: String? = null,
    val coupon: String = "",
    val isMigration: Boolean = false,
    val note: String = "",
) {
    fun isValid(): Boolean {
        // Implementación básica de validación
        val requiredFieldsValid = firstName.isNotBlank() && lastName.isNotBlank() && 
                dni.isNotBlank() && address.isNotBlank() && phone.isNotBlank()
        val noErrors = firstNameError == null && lastNameError == null && 
                dniError == null && addressError == null && phoneError == null && 
                planError == null
        
        // Verificar también que se haya seleccionado un plan
        val planSelected = selectedPlan != null
        
        return requiredFieldsValid && noErrors && planSelected
    }
}

