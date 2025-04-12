package com.dscorp.ispadmin.presentation.ui.features.subscription.register.models

import com.example.cleanarchitecture.domain.entity.InstallationType
import com.example.cleanarchitecture.domain.entity.NapBoxResponse
import com.example.cleanarchitecture.domain.entity.NetworkDevice
import com.example.cleanarchitecture.domain.entity.Onu
import com.example.cleanarchitecture.domain.entity.PlaceResponse
import com.example.cleanarchitecture.domain.entity.PlanResponse
import com.example.cleanarchitecture.domain.entity.extensions.isAValidAddress
import com.example.cleanarchitecture.domain.entity.extensions.isAValidName
import com.example.cleanarchitecture.domain.entity.extensions.isValidDni
import com.example.cleanarchitecture.domain.entity.extensions.isValidPhone
import com.google.android.gms.maps.model.LatLng

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
        val isFirstNameValid = firstName.isNotBlank() && firstName.isAValidName()
        val isLastNameValid = lastName.isNotBlank() && lastName.isAValidName()
        val isDniValid = dni.isNotBlank() && dni.isValidDni()
        val isAddressValid = address.isNotBlank() && address.isAValidAddress()
        val isPhoneValid = phone.isNotBlank() && phone.isValidPhone()

        val isPlanSelected = selectedPlan != null
        val isPlaceSelected = selectedPlace != null


        // Si es fibra, ONU y NapBox son obligatorios
        val isFiberPlan = selectedPlan?.type == InstallationType.FIBER
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
