package com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUseCase
import com.example.cleanarchitecture.domain.entity.GeoLocation
import com.example.cleanarchitecture.domain.entity.InstallationType
import com.example.cleanarchitecture.domain.entity.NapBoxResponse
import com.example.cleanarchitecture.domain.entity.NetworkDevice
import com.example.cleanarchitecture.domain.entity.Onu
import com.example.cleanarchitecture.domain.entity.PlaceResponse
import com.example.cleanarchitecture.domain.entity.PlanResponse
import com.example.cleanarchitecture.domain.entity.Subscription
import com.example.cleanarchitecture.domain.entity.User
import com.example.cleanarchitecture.domain.entity.extensions.isAValidAddress
import com.example.cleanarchitecture.domain.entity.extensions.isAValidName
import com.example.cleanarchitecture.domain.entity.extensions.isValidDni
import com.example.cleanarchitecture.domain.entity.extensions.isValidPhone
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
    private val getNapBoxListUseCase: GetNapBoxListUseCase,
    private val registerSubscriptionUseCase: RegisterSubscriptionUseCase,
    private val getUserSessionUseCase: GetUserSessionUseCase,
    private val getCoreDevicesUseCase: GetCoreDevicesUseCase
) : ViewModel() {

    val myUiState = MutableStateFlow(RegisterSubscriptionState())

    var myNapBoxList: List<NapBoxResponse> = emptyList()
        private set

    var myPlanList: List<PlanResponse> = emptyList()
        private set

    private var currentUser: User? = null

    fun getFormData() = viewModelScope.launch {
        myUiState.update {
            it.copy(isLoading = true)
        }

        try {
            val onuListDeferred = async { getAvailableOnuListUseCase() }
            val planListDeferred = async { getPlanListUseCase() }
            val placeListDeferred = async { getPlaceListUseCase() }
            val napBoxListDeferred = async { getNapBoxListUseCase() }
            val userSessionDeferred = async { getUserSessionUseCase() }
            val coreDevicesDeferred = async { getCoreDevicesUseCase() }


            val onuList = onuListDeferred.await()
            val planList = planListDeferred.await()
            val placeList = placeListDeferred.await()
            val napBoxList = napBoxListDeferred.await()
            val userSession = userSessionDeferred.await()
            val coreDevices = coreDevicesDeferred.await()

            currentUser = userSession.getOrThrow()

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
                        selectedHostDevice = coreDevices.getOrThrow()[0]
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

    fun onInstallationTypeSelected(type: InstallationType) {
        val filteredPlans = when (type) {
            InstallationType.FIBER -> myPlanList.filter { it.type == PlanResponse.PlanType.FIBER }
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
        val form = myUiState.value.registerSubscriptionForm

        // Validar el formulario utilizando el método isValid de RegisterSubscriptionFormState
        if (!form.isValid()) {
            return
        }

        // Mostrar indicador de carga
        myUiState.update {
            it.copy(isLoading = true)
        }

        // Construir el objeto Subscription
        val subscription = buildSubscriptionFromForm(form)

        // Llamar al caso de uso para registrar la suscripción
        viewModelScope.launch {
            registerSubscriptionUseCase(subscription).fold(
                onSuccess = { registeredSubscription ->
                    myUiState.update {
                        it.copy(
                            isLoading = false,
                            registeredSubscription = registeredSubscription,
                            error = ""
                        )
                    }
                },
                onFailure = { error ->
                    myUiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error al registrar la suscripción"
                        )
                    }
                }
            )
        }
    }

    private fun buildSubscriptionFromForm(form: RegisterSubscriptionFormState): Subscription {
        return Subscription(
            firstName = form.firstName,
            lastName = form.lastName,
            dni = form.dni,
            address = form.address,
            phone = form.phone,
            subscriptionDate = form.subscriptionDate,
            planId = form.selectedPlan!!.id,
            placeId = form.selectedPlace!!.id,
            technicianId = currentUser!!.id,
            hostDeviceId = form.selectedHostDevice!!.id,
            location = GeoLocation(
                form.location?.latitude ?: 0.0,
                form.location?.longitude ?: 0.0
            ),
            installationType = form.installationType,
            note = form.note
        )
    }
}

data class RegisterSubscriptionState(
    val isLoading: Boolean = false,
    val error: String = "",
    val registeredSubscription: Subscription? = null,
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
    val placeList: List<PlaceResponse> = emptyList(),
    val selectedPlace: PlaceResponse? = null,
    val placeError: String? = null,
    val selectedHostDevice: NetworkDevice? = null,
    val location: LatLng? = null,
    val cpeDevice: NetworkDevice? = null,
    val napBoxError: String? = null,
    val napBoxList: List<NapBoxResponse> = emptyList(),
    val selectedNapBox: NapBoxResponse? = null,
    val onuList: List<Onu> = emptyList(),
    val selectedOnu: Onu? = null,
    val onuError: String? = null,
    val coupon: String = "",
    val note: String = "",
    val installationType: InstallationType = InstallationType.FIBER,
) {
    fun isValid(): Boolean {
        // Verificar que todos los campos requeridos tengan valores válidos
        val isFirstNameValid = firstName.isNotBlank() && firstName.isAValidName()
        val isLastNameValid = lastName.isNotBlank() && lastName.isAValidName()
        val isDniValid = dni.isNotBlank() && dni.isValidDni()
        val isAddressValid = address.isNotBlank() && address.isAValidAddress()
        val isPhoneValid = phone.isNotBlank() && phone.isValidPhone()

        // Verificar que se hayan seleccionado los elementos requeridos
        val isPlanSelected = selectedPlan != null
        val isPlaceSelected = selectedPlace != null

        // Verificar si es instalación de fibra óptica y validar campos específicos
        val isFiberPlan = selectedPlan?.type == PlanResponse.PlanType.FIBER
        val fiberRequirementsValid = if (isFiberPlan) {
            selectedOnu != null && selectedNapBox != null
        } else {
            true // Si no es fibra óptica, estos campos no son obligatorios
        }

        // Verificar que no haya errores en los campos
        val noErrors = firstNameError == null && lastNameError == null &&
                dniError == null && addressError == null && phoneError == null &&
                planError == null && placeError == null

        return isFirstNameValid && isLastNameValid && isDniValid &&
                isAddressValid && isPhoneValid && isPlanSelected &&
                isPlaceSelected && fiberRequirementsValid && noErrors
    }
}
