package com.dscorp.ispadmin.domain.repository

import com.dscorp.ispadmin.domain.model.RegistrationCatalog
import kotlinx.coroutines.flow.Flow

interface RegistrationCatalogRepository {
    suspend fun getCachedCatalog(): Result<RegistrationCatalog>
    suspend fun refreshFromRemote(): Result<Unit>
    fun observeLastSync(): Flow<Long?>
}
