package com.dscorp.ispadmin.domain.usecase.catalog

import com.dscorp.ispadmin.domain.exception.CatalogNotAvailableOffline
import com.dscorp.ispadmin.domain.model.RegistrationCatalog
import com.dscorp.ispadmin.domain.repository.RegistrationCatalogRepository

class GetRegistrationCatalogUseCase(
    private val registrationCatalogRepository: RegistrationCatalogRepository
) {
    suspend operator fun invoke(): Result<RegistrationCatalog> = runCatching {
        val catalog = registrationCatalogRepository.getCachedCatalog().getOrElse { throw it }
        if (!catalog.isAvailableOffline()) {
            throw CatalogNotAvailableOffline()
        }
        catalog
    }
}
