package com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose

import com.example.cleanarchitecture.domain.entity.NapBoxResponse
import com.example.data2.data.repository.IRepository

class GetNearNapBoxesUseCase(val repository: IRepository) {

    suspend operator fun invoke(latitude: Double, longitude: Double): Result<List<NapBoxResponse>> =
        runCatching {
            repository.getNapBoxesOrderedByLocation(latitude, longitude)
        }

}
