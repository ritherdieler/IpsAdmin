package com.dscorp.ispadmin.presentation.ui.features.subscription.register.mapper

import com.dscorp.ispadmin.domain.model.CatalogCoreDevice
import com.dscorp.ispadmin.domain.model.CatalogNapBox
import com.dscorp.ispadmin.domain.model.CatalogOnu
import com.dscorp.ispadmin.domain.model.CatalogPlace
import com.dscorp.ispadmin.domain.model.CatalogPlan
import com.dscorp.ispadmin.domain.model.InstallationType
import com.dscorp.ispadmin.domain.model.NapBoxResponse
import com.dscorp.ispadmin.domain.model.NetworkDevice
import com.dscorp.ispadmin.domain.model.Onu
import com.dscorp.ispadmin.domain.model.Place
import com.dscorp.ispadmin.domain.model.PlanResponse

fun CatalogPlan.toPlanResponse(): PlanResponse = PlanResponse(
    id = id,
    name = name,
    price = price,
    downloadSpeed = downloadSpeed,
    uploadSpeed = uploadSpeed,
    type = type?.let { runCatching { InstallationType.valueOf(it) }.getOrNull() }
        ?: InstallationType.FIBER
)

fun CatalogPlace.toPlace(): Place = Place(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude
)

fun CatalogNapBox.toNapBoxResponse(): NapBoxResponse = NapBoxResponse(
    id = id,
    code = code,
    address = address,
    mufaId = mufaId,
    latitude = latitude,
    longitude = longitude,
    ports_number = portsNumber,
    placeName = placeName,
    placeId = placeId
)

fun CatalogOnu.toOnu(): Onu = Onu(
    board = board,
    olt_id = oltId,
    onu = onu,
    onu_type_id = onuTypeId,
    onu_type_name = onuTypeName,
    pon_type = ponType,
    port = port,
    sn = sn
)

fun CatalogCoreDevice.toNetworkDevice(): NetworkDevice = NetworkDevice(
    id = id,
    name = name,
    password = password,
    username = username,
    ipAddress = ipAddress,
    networkDeviceType = networkDeviceType?.let {
        runCatching { NetworkDevice.NetworkDeviceType.valueOf(it) }.getOrNull()
    },
    disabled = disabled
)
