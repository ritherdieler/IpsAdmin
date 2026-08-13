package com.dscorp.ispadmin.data.local.mapper

import com.dscorp.ispadmin.data.local.entity.CatalogCoreDeviceEntity
import com.dscorp.ispadmin.data.local.entity.CatalogNapBoxEntity
import com.dscorp.ispadmin.data.local.entity.CatalogOnuEntity
import com.dscorp.ispadmin.data.local.entity.CatalogPlaceEntity
import com.dscorp.ispadmin.data.local.entity.CatalogPlanEntity
import com.dscorp.ispadmin.domain.model.CatalogCoreDevice
import com.dscorp.ispadmin.domain.model.CatalogNapBox
import com.dscorp.ispadmin.domain.model.CatalogOnu
import com.dscorp.ispadmin.domain.model.CatalogPlace
import com.dscorp.ispadmin.domain.model.CatalogPlan

fun CatalogPlan.toEntity() = CatalogPlanEntity(
    id = id,
    name = name,
    price = price,
    downloadSpeed = downloadSpeed,
    uploadSpeed = uploadSpeed,
    type = type
)

fun CatalogPlanEntity.toDomain() = CatalogPlan(
    id = id,
    name = name,
    price = price,
    downloadSpeed = downloadSpeed,
    uploadSpeed = uploadSpeed,
    type = type
)

fun CatalogPlace.toEntity() = CatalogPlaceEntity(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude
)

fun CatalogPlaceEntity.toDomain() = CatalogPlace(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude
)

fun CatalogNapBox.toEntity() = CatalogNapBoxEntity(
    id = id,
    code = code,
    address = address,
    mufaId = mufaId,
    latitude = latitude,
    longitude = longitude,
    portsNumber = portsNumber,
    placeName = placeName,
    placeId = placeId
)

fun CatalogNapBoxEntity.toDomain() = CatalogNapBox(
    id = id,
    code = code,
    address = address,
    mufaId = mufaId,
    latitude = latitude,
    longitude = longitude,
    portsNumber = portsNumber,
    placeName = placeName,
    placeId = placeId
)

fun CatalogOnu.toEntity() = CatalogOnuEntity(
    sn = sn,
    board = board,
    oltId = oltId,
    onu = onu,
    onuTypeId = onuTypeId,
    onuTypeName = onuTypeName,
    ponType = ponType,
    port = port
)

fun CatalogOnuEntity.toDomain() = CatalogOnu(
    sn = sn,
    board = board,
    oltId = oltId,
    onu = onu,
    onuTypeId = onuTypeId,
    onuTypeName = onuTypeName,
    ponType = ponType,
    port = port
)

fun CatalogCoreDevice.toEntity() = CatalogCoreDeviceEntity(
    id = id,
    name = name,
    password = password,
    username = username,
    ipAddress = ipAddress,
    networkDeviceType = networkDeviceType,
    disabled = disabled
)

fun CatalogCoreDeviceEntity.toDomain() = CatalogCoreDevice(
    id = id,
    name = name,
    password = password,
    username = username,
    ipAddress = ipAddress,
    networkDeviceType = networkDeviceType,
    disabled = disabled
)
