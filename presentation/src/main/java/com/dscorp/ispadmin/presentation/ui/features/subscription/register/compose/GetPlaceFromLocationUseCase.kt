package com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose

import com.example.cleanarchitecture.domain.domain.entity.PlaceResponse
import com.example.data2.data.repository.IRepository

class GetPlaceFromLocationUseCase(val repository: IRepository) {

    suspend operator fun invoke(latitude: Double, longitude: Double): Result<PlaceResponse> {
        return try {
            val planList = repository.getPlaceFromLocation(latitude, longitude)
            Result.success(planList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}