package com.dscorp.ispadmin.domain.usecase.catalog

import com.dscorp.ispadmin.domain.repository.RegistrationCatalogRepository

class RefreshRegistrationCatalogUseCase(
    private val registrationCatalogRepository: RegistrationCatalogRepository
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        registrationCatalogRepository.refreshFromRemote().getOrThrow()
    }
}
