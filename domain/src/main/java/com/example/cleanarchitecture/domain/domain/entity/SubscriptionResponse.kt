package com.example.cleanarchitecture.domain.domain.entity

import com.example.cleanarchitecture.domain.domain.entity.extensions.toFormattedDateString
import java.text.SimpleDateFormat
import java.util.*

data class SubscriptionResponse(
    var id: Int? = null,
    var address: String? = null,
    var dni: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var location: GeoLocation? = null,
    var napBox: NapBoxResponse? = null,
    var networkDevices: List<NetworkDevice>? = null,
    var new: Boolean? = null,
    var password: String? = null,
    var phone: String? = null,
    var place: PlaceResponse? = null,
    var plan: PlanResponse? = null,
    var ip: String? = null,
    var serviceIsSuspended: Boolean? = null,
    var technician: Technician? = null,
    var hostDevice: NetworkDevice? = null,
    var subscriptionDate: Long? = null,
    var isMigration: Boolean,
    var price: Double? = null,
) : java.io.Serializable {
    fun getFullName() = "$firstName $lastName"

    fun migrationAsString() = if (isMigration) "Si" else "No"

    fun dateAsString() = subscriptionDate?.toFormattedDateString()

}
