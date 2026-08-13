package com.dscorp.ispadmin.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface RegistrationCatalogApi {
    @GET("plan")
    suspend fun getPlans(): Response<List<CatalogPlanDto>>

    @GET("place")
    suspend fun getPlaces(): Response<List<CatalogPlaceDto>>

    @GET("napbox")
    suspend fun getNapBoxes(): Response<List<CatalogNapBoxDto>>

    @GET("onu/unconfigured_onus")
    suspend fun getUnconfiguredOnus(): Response<List<CatalogOnuDto>>

    @GET("networkDevice/coreTypes")
    suspend fun getCoreDevices(): Response<List<CatalogCoreDeviceDto>>
}

data class CatalogPlanDto(
    val id: String? = null,
    val name: String? = null,
    val price: Double? = null,
    val downloadSpeed: String? = null,
    val uploadSpeed: String? = null,
    val type: String? = null
)

data class CatalogPlaceDto(
    val id: String? = null,
    val name: String? = null,
    val latitude: Float? = null,
    val longitude: Float? = null
)

data class CatalogNapBoxDto(
    val id: String? = null,
    val code: String = "",
    val address: String = "",
    val mufaId: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val ports_number: Int? = null,
    val placeName: String = "",
    val placeId: Int = 0
)

data class CatalogOnuDto(
    val board: String = "",
    val olt_id: String = "",
    val onu: String = "",
    val onu_type_id: String = "",
    val onu_type_name: String = "",
    val pon_type: String = "",
    val port: String = "",
    val sn: String = ""
)

data class CatalogCoreDeviceDto(
    val id: Int? = null,
    val name: String = "",
    val password: String = "",
    val username: String = "",
    val ipAddress: String = "",
    val networkDeviceType: String? = null,
    val disabled: Boolean = false
)
