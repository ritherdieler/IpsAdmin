package com.dscorp.ispadmin.data.remote

import com.dscorp.ispadmin.domain.model.CatalogCoreDevice
import com.dscorp.ispadmin.domain.model.CatalogNapBox
import com.dscorp.ispadmin.domain.model.CatalogOnu
import com.dscorp.ispadmin.domain.model.CatalogPlace
import com.dscorp.ispadmin.domain.model.CatalogPlan

interface RegistrationCatalogRemoteDataSource {
    suspend fun fetchPlans(): List<CatalogPlan>
    suspend fun fetchPlaces(): List<CatalogPlace>
    suspend fun fetchNapBoxes(): List<CatalogNapBox>
    suspend fun fetchOnus(): List<CatalogOnu>
    suspend fun fetchCoreDevices(): List<CatalogCoreDevice>
}
