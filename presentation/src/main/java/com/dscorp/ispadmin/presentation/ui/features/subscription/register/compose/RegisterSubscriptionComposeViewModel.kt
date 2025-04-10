package com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUseCase
import com.example.cleanarchitecture.domain.entity.*
import com.example.cleanarchitecture.domain.entity.extensions.isAValidAddress
import com.example.cleanarchitecture.domain.entity.extensions.isAValidName
import com.example.cleanarchitecture.domain.entity.extensions.isValidDni
import com.example.cleanarchitecture.domain.entity.extensions.isValidPhone
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsable de manejar toda la lógica relacionada con el
 * registro de suscripciones. Se encarga de cargar datos iniciales,
 * validar los campos del formulario y finalmente registrar la suscripción.
 *
 * @property getAvailableOnuListUseCase Caso de uso para obtener los ONUs disponibles.
 * @property getPlanListUseCase Caso de uso para obtener el listado de planes.
 * @property getPlaceListUseCase Caso de uso para obtener el listado de lugares.
 * @property getPlaceFromLocationUseCase Caso de uso para obtener un lugar dada una ubicación.
 * @property getNapBoxListUseCase Caso de uso para obtener el listado de cajas Nap.
 * @property registerSubscriptionUseCase Caso de uso para registrar la suscripción.
 * @property getUserSessionUseCase Caso de uso para obtener la información de usuario en sesión.
 * @property getCoreDevicesUseCase Caso de uso para obtener dispositivos de red principales.
 */
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

    /**
     * Flujo de estado principal que notifica a la UI
     * acerca de los cambios en el proceso de registro de suscripciones.
     */
    val uiState = MutableStateFlow(RegisterSubscriptionState())

    /**
     * Listado en caché de NapBoxes. Se actualiza una vez cargados los datos.
     */
    private var cachedNapBoxList: List<NapBoxResponse> = emptyList()

    /**
     * Listado en caché de planes. Se actualiza una vez cargados los datos.
     */
    private var cachedPlanList: List<PlanResponse> = emptyList()

    /**
     * Usuario actual en sesión. Se actualiza al cargar los datos.
     */
    private var currentUser: User? = null

    /**
     * Carga la información inicial del formulario de registro.
     * Obtiene planes, lugares, ONUs disponibles, cajas Nap, usuario en sesión
     * y dispositivos de red principales en paralelo.
     */
    fun loadInitialFormData() = viewModelScope.launch {
        uiState.update { it.copy(isLoading = true) }

        try {
            // Lanzamos corrutinas en paralelo para mejorar el rendimiento
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
            cachedNapBoxList = napBoxList.getOrNull() ?: emptyList()
            cachedPlanList = planList.getOrNull() ?: emptyList()

            uiState.update { current ->
                current.copy(
                    isLoading = false,
                    registerSubscriptionForm = current.registerSubscriptionForm.copy(
                        onuList = onuList.getOrNull() ?: emptyList(),
                        planList = cachedPlanList.filter { it.type == PlanResponse.PlanType.FIBER },
                        placeList = placeList.getOrNull() ?: emptyList(),
                        napBoxList = cachedNapBoxList,
                        selectedHostDevice = coreDevices.getOrThrow().firstOrNull()
                    )
                )
            }
        } catch (e: Exception) {
            uiState.update { current ->
                current.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    /**
     * Manejador genérico para actualizar campos del formulario.
     * Utiliza un mapa de claves para identificar los campos y sus validaciones.
     */
    fun <T> updateField(
        fieldKey: FormFieldKey,
        value: T,
        isValid: (T) -> Boolean,
        errorMessage: String? = null
    ) {
        uiState.update { currentState ->
            val form = currentState.registerSubscriptionForm
            val updatedForm = when (fieldKey) {
                FormFieldKey.FIRST_NAME -> form.copy(
                    firstName = value as String,
                    firstNameError = if (isValid(value)) null else errorMessage
                )
                FormFieldKey.LAST_NAME -> form.copy(
                    lastName = value as String,
                    lastNameError = if (isValid(value)) null else errorMessage
                )
                FormFieldKey.DNI -> form.copy(
                    dni = value as String,
                    dniError = if (isValid(value)) null else errorMessage
                )
                FormFieldKey.ADDRESS -> form.copy(
                    address = value as String,
                    addressError = if (isValid(value)) null else errorMessage
                )
                FormFieldKey.PHONE -> form.copy(
                    phone = value as String,
                    phoneError = if (isValid(value)) null else errorMessage
                )
                FormFieldKey.PLAN -> form.copy(
                    selectedPlan = value as PlanResponse,
                    planError = if (isValid(value)) null else errorMessage
                )
                FormFieldKey.PLACE -> form.copy(
                    selectedPlace = value as PlaceResponse,
                    placeError = if (isValid(value)) null else errorMessage,
                    napBoxList = cachedNapBoxList.filter { it.placeId == value.id?.toInt() }
                )
                FormFieldKey.ONU -> form.copy(
                    selectedOnu = value as Onu,
                    onuError = if (isValid(value)) null else errorMessage
                )
                FormFieldKey.NAP_BOX -> form.copy(
                    selectedNapBox = value as NapBoxResponse,
                    napBoxError = if (isValid(value)) null else errorMessage
                )
            }
            currentState.copy(registerSubscriptionForm = updatedForm)
        }
    }

    // Enum para identificar los campos del formulario
    enum class FormFieldKey {
        FIRST_NAME,
        LAST_NAME,
        DNI,
        ADDRESS,
        PHONE,
        PLAN,
        PLACE,
        ONU,
        NAP_BOX
    }

    // Ejemplo de uso para actualizar campos
    fun onFirstNameChanged(value: String) {
        updateField(
            fieldKey = FormFieldKey.FIRST_NAME,
            value = value,
            isValid = { it.isAValidName() },
            errorMessage = "Nombre inválido"
        )
    }

    fun onLastNameChanged(value: String) {
        updateField(
            fieldKey = FormFieldKey.LAST_NAME,
            value = value,
            isValid = { it.isAValidName() },
            errorMessage = "Apellido inválido"
        )
    }

    fun onDniChanged(value: String) {
        updateField(
            fieldKey = FormFieldKey.DNI,
            value = value,
            isValid = { it.isValidDni() },
            errorMessage = "DNI inválido"
        )
    }

    fun onAddressChanged(value: String) {
        updateField(
            fieldKey = FormFieldKey.ADDRESS,
            value = value,
            isValid = { it.isAValidAddress() },
            errorMessage = "Dirección inválida"
        )
    }

    fun onPhoneChanged(value: String) {
        updateField(
            fieldKey = FormFieldKey.PHONE,
            value = value,
            isValid = { it.isValidPhone() },
            errorMessage = "Número de teléfono inválido"
        )
    }

    fun onPlanSelected(value: PlanResponse) {
        updateField(
            fieldKey = FormFieldKey.PLAN,
            value = value,
            isValid = { it != null },
            errorMessage = "Debe seleccionar un plan"
        )
    }

    fun onPlaceSelected(value: PlaceResponse) {
        updateField(
            fieldKey = FormFieldKey.PLACE,
            value = value,
            isValid = { it != null },
            errorMessage = "Debe seleccionar un lugar"
        )
    }

    fun onOnuSelected(value: Onu) {
        updateField(
            fieldKey = FormFieldKey.ONU,
            value = value,
            isValid = { it != null },
            errorMessage = "Debe seleccionar un ONU"
        )
    }

    fun onNapBoxSelected(value: NapBoxResponse) {
        updateField(
            fieldKey = FormFieldKey.NAP_BOX,
            value = value,
            isValid = { it != null },
            errorMessage = "Debe seleccionar un NapBox"
        )
    }

    fun onPlaceSelectionCleared() {
        uiState.update {
            it.copy(
                registerSubscriptionForm = it.registerSubscriptionForm.copy(
                    selectedPlace = null,
                    placeError = null
                )
            )
        }
    }

    fun onNapBoxSelectionCleared() {
        uiState.update {
            it.copy(
                registerSubscriptionForm = it.registerSubscriptionForm.copy(
                    selectedNapBox = null,
                    napBoxError = null
                )
            )
        }
    }

    /**
     * Cambia el tipo de instalación y filtra la lista de planes según el tipo seleccionado.
     */
    fun onInstallationTypeSelected(type: InstallationType) {
        val filteredPlans = when (type) {
            InstallationType.FIBER -> cachedPlanList.filter { it.type == PlanResponse.PlanType.FIBER }
            else -> cachedPlanList.filter { it.type == PlanResponse.PlanType.WIRELESS }
        }

        uiState.update {
            it.copy(
                registerSubscriptionForm = it.registerSubscriptionForm.copy(
                    planList = filteredPlans,
                    selectedPlan = null // Se limpia la selección de plan si cambia el tipo
                )
            )
        }
    }

    /**
     * Obtiene un lugar a partir de coordenadas de ubicación.
     */
    fun getPlaceFromCurrentLocation(latitude: Double, longitude: Double) = viewModelScope.launch {
        getPlaceFromLocationUseCase(latitude, longitude).fold(
            onSuccess = { place ->
                uiState.update {
                    it.copy(
                        registerSubscriptionForm = it.registerSubscriptionForm.copy(
                            selectedPlace = place
                        )
                    )
                }
            },
            onFailure = { error ->
                uiState.update {
                    it.copy(
                        error = error.message ?: "Unknown error"
                    )
                }
            }
        )
    }

    /**
     * Inicia el proceso de guardado de la suscripción.
     * Primero se valida el formulario, luego se construye un objeto Subscription
     * y se realiza el registro a través del caso de uso [registerSubscriptionUseCase].
     */
    fun saveSubscription() {
        val form = uiState.value.registerSubscriptionForm

        // Validar el formulario con isValid()
        if (!form.isValid()) {
            return
        }

        // Mostrar indicador de carga
        uiState.update {
            it.copy(isLoading = true)
        }

        // Construir el objeto Subscription
        val subscription = createSubscriptionFromForm(form)

        // Registrar la suscripción
        viewModelScope.launch {
            registerSubscriptionUseCase(subscription).fold(
                onSuccess = { registeredSubscription ->
                    uiState.update {
                        it.copy(
                            isLoading = false,
                            registeredSubscription = registeredSubscription,
                            error = ""
                        )
                    }
                },
                onFailure = { error ->
                    uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error al registrar la suscripción"
                        )
                    }
                }
            )
        }
    }

    /**
     * Crea una instancia de [Subscription] a partir de los datos del formulario.
     */
    private fun createSubscriptionFromForm(
        form: RegisterSubscriptionFormState
    ): Subscription {
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
            hostDeviceId = form.selectedHostDevice?.id,
            location = GeoLocation(
                form.location?.latitude ?: 0.0,
                form.location?.longitude ?: 0.0
            ),
            installationType = form.installationType,
            note = form.note
        )
    }
}

/**
 * Estado principal del ViewModel, que encapsula la información
 * sobre la carga de datos, errores y el estado actual del formulario.
 */
data class RegisterSubscriptionState(
    val isLoading: Boolean = false,
    val error: String = "",
    val registeredSubscription: Subscription? = null,
    val registerSubscriptionForm: RegisterSubscriptionFormState = RegisterSubscriptionFormState()
)

/**
 * Representa el estado del formulario de registro y
 * provee la lógica de validación central.
 */
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
    /**
     * Valida todos los campos obligatorios y retorna true si
     * el formulario está listo para ser enviado.
     */
    fun isValid(): Boolean {
        val isFirstNameValid = firstName.isNotBlank() && firstName.isAValidName()
        val isLastNameValid = lastName.isNotBlank() && lastName.isAValidName()
        val isDniValid = dni.isNotBlank() && dni.isValidDni()
        val isAddressValid = address.isNotBlank() && address.isAValidAddress()
        val isPhoneValid = phone.isNotBlank() && phone.isValidPhone()

        val isPlanSelected = selectedPlan != null
        val isPlaceSelected = selectedPlace != null

        // Si es fibra, ONU y NapBox son obligatorios
        val isFiberPlan = selectedPlan?.type == PlanResponse.PlanType.FIBER
        val fiberRequirementsValid = if (isFiberPlan) {
            selectedOnu != null && selectedNapBox != null
        } else {
            true
        }

        val noErrors = firstNameError == null &&
                lastNameError == null &&
                dniError == null &&
                addressError == null &&
                phoneError == null &&
                planError == null &&
                placeError == null

        return isFirstNameValid && isLastNameValid && isDniValid &&
                isAddressValid && isPhoneValid && isPlanSelected &&
                isPlaceSelected && fiberRequirementsValid && noErrors
    }
}
